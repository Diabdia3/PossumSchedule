function editTask(button) {
    var taskId = button.getAttribute("taskId");
    window.location.replace("http://localhost:8080/tasks/task/" + taskId + "/edit");
}

function completeTask(button) {
    if(!confirm('Do you want to COMPLETE the task?'))
            return;
    var taskId = button.getAttribute("taskId");
    const data = new URLSearchParams();
    data.append('taskId', taskId);
    fetch("http://localhost:8080/tasks/complete", {
    method: "POST",
    body: data
    })
    .then(response => handleResponce(response, 'complete'))
    .catch(error => {
    window.alert('Error occurred D:');
    console.error("Error:", error);
    });
}

function removeTask(button) {
    if(!confirm('Do you want to REMOVE the task?'))
            return;
    var taskId = button.getAttribute("taskId");
    const data = new URLSearchParams();
    data.append('taskId', taskId);
    fetch("http://localhost:8080/tasks/remove", {
    method: "POST",
    body: data
    })
    .then(response => handleResponce(response, 'remove'))
    .catch(error => {
    window.alert('Error occurred D:');
    console.error("Error:", error);
    });
}

function handleResponce(res, action) {
    if(!res.ok){
        throw res.status;
        return;
    }
    if(action == 'remove'){
        window.location.replace("http://localhost:8080/tasks/all");
    }
}