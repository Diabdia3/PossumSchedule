function handleTaskClick(task) {
	var taskId = task.getAttribute("taskId");
    window.location.replace("http://localhost:8080/tasks/task/" + taskId);
}

function removeTask(button) {
    stopBubbling(this.event);
    if(!confirm('Do you want to REMOVE the task?'))
            return;
    var taskId = button.getAttribute("taskId");
    const data = new URLSearchParams();
    data.append('taskId', taskId);
    fetch("http://localhost:8080/tasks/remove", {
    method: "POST",
    body: data
    })
    .then(response => handleResponce(response, button))
    .catch(error => {
    window.alert('Error occurred D:');
    console.error("Error:", error);
    });
}

function incompleteTask(button) {
    stopBubbling(this.event);
    if(!confirm('Do you want to INCOMPLETE the task?'))
            return;
    var taskId = button.getAttribute("taskId");
    const data = new URLSearchParams();
    data.append('taskId', taskId);
    fetch("http://localhost:8080/tasks/incomplete", {
    method: "POST",
    body: data
    })
    .then(response => handleResponce(response, button))
    .catch(error => {
    window.alert('Error occurred D:');
    console.error("Error:", error);
    });
}

function handleResponce(res, button) {
    if(!res.ok){
        throw res.status;
        return;
    }
    button.parentNode.parentNode.remove();
}

function stopBubbling(evt){
    evt.stopPropagation();
    evt.cancelBubble = true;
}