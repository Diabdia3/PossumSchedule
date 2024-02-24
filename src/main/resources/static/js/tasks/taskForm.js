function setTaskId(input){
    var taskId = input.getAttribute("taskId");
    input.value = taskId;
}

function resize(){
    setTimeout(resize(document.getElementById("content")), 10);
}

function resize(textarea){
    if(textarea == undefined)
        textarea = document.getElementById("content");
    textarea.style.height = "";
    textarea.style.height = textarea.scrollHeight + "px";
}


