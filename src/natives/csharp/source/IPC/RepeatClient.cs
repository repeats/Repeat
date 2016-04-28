using log4net;
using Newtonsoft.Json.Linq;
using Repeat.IPC;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Repeat.ipc {
    public class RepeatClient {

        private static readonly ILog logger = LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
        private const int MAX_BUFFER_SIZE = 1024;
        private const int REPEAT_SERVER_TIMEOUT_MS = 10 * 1000;
        private const int REPEAT_CLIENT_TIMEOUT_MS = (int)(REPEAT_SERVER_TIMEOUT_MS * 0.8);
        public const char REPEAT_DELIMITER = '\x02';
        
        private Socket socket;
        private bool IsTerminated { get; set; }
        private Thread readThread, writeThread;

        public Dictionary<int, AutoResetEvent> synchronizationEvents;
        public Dictionary<int, JToken> returnedObjects;
        public ConcurrentQueue<string> sendQueue;
        public AutoResetEvent sendSignal;

        protected MessageProcessor messageProcessor;

        protected SystemClientRequest systemClient;
        protected SystemHostRequest systemHost;
        public MouseRequest mouse { get; private set; }
        public KeyboardRequest key { get; private set; }

        public RepeatClient() {
            synchronizationEvents = new Dictionary<int, AutoResetEvent>();
            returnedObjects = new Dictionary<int, JToken>();

            sendQueue = new ConcurrentQueue<string>();
            sendSignal = new AutoResetEvent(false);

            messageProcessor = new MessageProcessor(this);

            systemClient = new SystemClientRequest(this);
            systemHost = new SystemHostRequest(this);
            mouse = new MouseRequest(this);
            key = new KeyboardRequest(this);
        }

        private void ReadThread() {
            byte[] received = new byte[MAX_BUFFER_SIZE];
            while (!IsTerminated) {
                try {
                    if (!IsConnected()) {
                        logger.Info("Socket not connected. Terminating...");
                        this.IsTerminated = true;
                        break;
                    }
                    int size = socket.Receive(received);
                    messageProcessor.process(received, size);
                } catch (ThreadInterruptedException) {
                    logger.Info("Read thread interrupted. Terminating...");
                    break;
                } catch (Exception e) {
                    logger.Warn("Exception while receving data.", e);
                }
            }
            logger.Info("Read thread terminated.");
        }

        private void WriteThread() {
            string toSend;
            while (!IsTerminated) {
                toSend = null;
                try {
                    sendSignal.WaitOne(REPEAT_CLIENT_TIMEOUT_MS);
                } catch (ThreadInterruptedException) {//Signalled to quit.
                    break;
                }

                if (sendQueue.TryDequeue(out toSend)) {
                    try {
                        string sending = String.Format("{0}{1}{2}{3}{4}", REPEAT_DELIMITER, REPEAT_DELIMITER, toSend, REPEAT_DELIMITER, REPEAT_DELIMITER);
                        byte[] rawData = System.Text.Encoding.ASCII.GetBytes(sending);
                        socket.Send(rawData);
                    } catch (ThreadInterruptedException) {
                        logger.Info("Write thread interrupted. Terminating...");
                    } catch (Exception e) {
                        logger.Warn("Exception while sending message.", e);
                    }
                } else {
                    systemHost.KeepAlive();
                }
            }
            logger.Info("Write thread terminated.");
        }

        public void StartRunning() {
            if (IsRunning()) {
                logger.Warn("Cannot start client. Repeat client already running.");
                return;
            }
            synchronizationEvents.Clear();
            IsTerminated = false;
            readThread = new Thread(ReadThread);
            writeThread = new Thread(WriteThread);

            string host = "localhost";
            socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            socket.ReceiveBufferSize = MAX_BUFFER_SIZE;
            logger.Info("Establishing connection to " + host);

            try {
                socket.Connect(host, 9999);
                logger.Info("Connection established.");
            } catch (SocketException) {
                logger.Fatal("Unable to connect to Repeat server. Terminating...");
                return;
            }

            systemClient.Identify();

            readThread.Start();
            writeThread.Start();
        }

        public void StopRunning() {
            if (!IsRunning()) {
                return;
            }

            synchronizationEvents.Clear();
            IsTerminated = true;
            if (readThread != null) {
                readThread.Interrupt();
                readThread.Join();
            }

            if (writeThread != null) {
                writeThread.Interrupt();
                writeThread.Join();
            }
            socket.Close();
            Console.WriteLine("Stopped");
        }

        public bool IsRunning() {
            return (readThread != null && writeThread != null) && 
                (readThread.IsAlive || writeThread.IsAlive);
        }

        private bool IsConnected() {
            if (socket == null) {
                return false;
            }
            try {
                return !(socket.Poll(1, SelectMode.SelectRead) && socket.Available == 0);
            } catch (SocketException) {
                return false; 
            }
        }
    }
}
