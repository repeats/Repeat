package core.ipc.repeatClient.repeatPeerClient.api;

import java.awt.Color;
import java.awt.Point;
import java.util.List;

import argo.jdom.JsonNode;
import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatServer.processors.IpcMessageType;

public class RepeatsMouseControllerApi extends AbstractRepeatsClientApi {

	protected RepeatsMouseControllerApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		super(repeatPeerServiceClientWriter);
	}

	public void press(int... mask) {
		DeviceCommand command = commandBuilder().action("press").parameters(mask).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void release(int... mask) {
		DeviceCommand command = commandBuilder().action("release").parameters(mask).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void leftClick() {
		DeviceCommand command = commandBuilder().action("left_click").build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void leftClick(int delay) {
		DeviceCommand command = commandBuilder().action("left_click").parameters(delay).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void leftClick(int x, int y) {
		DeviceCommand command = commandBuilder().action("left_click").parameters(x, y).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void rightClick() {
		DeviceCommand command = commandBuilder().action("right_click").build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void rightClick(int delay) {
		DeviceCommand command = commandBuilder().action("right_click").parameters(delay).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void rightClick(int x, int y) {
		DeviceCommand command = commandBuilder().action("right_click").parameters(x, y).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void move(int x, int y) {
		DeviceCommand command = commandBuilder().action("move").parameters(x, y).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void moveBy(int x, int y) {
		DeviceCommand command = commandBuilder().action("move_by").parameters(x, y).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void drag(int x, int y) {
		DeviceCommand command = commandBuilder().action("drag").parameters(x, y).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void drag(int fromX, int fromY, int toX, int toY) {
		DeviceCommand command = commandBuilder().action("drag").parameters(fromX, fromY, toX, toY).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void dragBy(int x, int y) {
		DeviceCommand command = commandBuilder().action("drag_by").parameters(x, y).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public Point getPosition() {
		DeviceCommand command = commandBuilder().action("get_position").build();
		JsonNode response = waitAndGetJsonResponseIfSuccess(IpcMessageType.ACTION, command);
		List<JsonNode> nodes = response.getArrayNode();
		if (nodes.size() != 2) {
			throw new RuntimeException("Expecting response with 2 parameters. Got " + nodes.size());
		}

		int x = Integer.parseInt(nodes.get(0).getNumberValue());
		int y = Integer.parseInt(nodes.get(1).getNumberValue());
		return new Point(x, y);
	}

	public Color getColor() {
		DeviceCommand command = commandBuilder().action("get_color").build();
		JsonNode response = waitAndGetJsonResponseIfSuccess(IpcMessageType.ACTION, command);
		List<JsonNode> nodes = response.getArrayNode();
		if (nodes.size() != 3) {
			throw new RuntimeException("Expecting response with 3 parameters. Got " + nodes.size());
		}

		int r = Integer.parseInt(nodes.get(0).getNumberValue());
		int g = Integer.parseInt(nodes.get(1).getNumberValue());
		int b = Integer.parseInt(nodes.get(2).getNumberValue());
		return new Color(r, g, b);
	}

	private DeviceCommandBuilder commandBuilder() {
		return DeviceCommandBuilder.of().device("mouse");
	}
}
