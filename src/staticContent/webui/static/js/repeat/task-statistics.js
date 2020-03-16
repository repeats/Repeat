function _drawPastRun() {
    var encodedData = document.getElementById('task-execution-data').innerHTML;
    var data = atob(encodedData);
    var dataObject = JSON.parse(data);
    var instances = dataObject.executionInstances;

    var labels = [];
    var data = [];
    for (let i = 0; i < instances.length; i++) {
        labels.push(instances[i].start);
        data.push(instances[i].duration);
    }

    var ctx = document.getElementById('myChart').getContext('2d');
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Recent run durations',
                data: data,
                backgroundColor: 'rgba(0, 255, 55, 0.9)',
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    },
                    scaleLabel: {
                        display: true,
                        labelString: 'Execution time (ms)'
                    }
                }]
            }
        }
    });
}
