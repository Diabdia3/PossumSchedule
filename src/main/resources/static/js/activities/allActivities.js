var cur = 0;
var editState = false;
var tempName;
var tempDescription;
var tempStartTime;
var tempEndTime;
var tempRepetition;
var tempRDays;

window.onload = function(){
    var list = document.getElementById("activityList");
    var current = list.getAttribute("current");
    cur = current;
    var wrappers = list.children;
    for(var i = 0; i < wrappers.length-2; i++){
        if(current != i)
            continue;
        wrappers[i].classList.add('mainWrapper');
        if(i > 0){
            wrappers[i-1].classList.add('leftWrapper');
            wrappers[i-1].children[0].classList.add('leftAct');
            document.getElementById("slideLeft").onclick = previous;
        }
        if(i < wrappers.length-3){
            wrappers[i+1].classList.add('rightWrapper');
            wrappers[i+1].children[0].classList.add('rightAct');
            document.getElementById("slideRight").onclick = next;
        }
    }
}

function next() {
        var list = document.getElementById("activityList");
        var wrappers = list.children;
        if(cur+1 == wrappers.length-2)
        return;
        cur++;

        if(cur-2 >= 0){
            wrappers[cur-2].classList.remove('leftWrapper');
            wrappers[cur-2].children[0].classList.remove('leftAct');
        }

        wrappers[cur].classList.remove('rightWrapper');
        wrappers[cur].children[0].classList.remove('rightAct');
        wrappers[cur].classList.add('mainWrapper');

        wrappers[cur-1].classList.remove('mainWrapper');
        wrappers[cur-1].classList.add('leftWrapper');
        wrappers[cur-1].children[0].classList.add('leftAct')
        document.getElementById("slideLeft").onclick = previous;

        if(cur < wrappers.length-3){
            wrappers[cur+1].classList.add('rightWrapper');
            wrappers[cur+1].children[0].classList.add('rightAct');
            document.getElementById("slideRight").onclick = next;
        } else {
            document.getElementById("slideRight").onclick = null;
        }
}

function previous(){
        var list = document.getElementById("activityList");
        var wrappers = list.children;
        if(cur == 0)
            return;
        cur--;

        if(cur+2 < wrappers.length-2){
            wrappers[cur+2].classList.remove('rightWrapper');
            wrappers[cur+2].children[0].classList.remove('rightAct');
        }

        wrappers[cur].classList.remove('leftWrapper');
        wrappers[cur].children[0].classList.remove('leftAct');
        wrappers[cur].classList.add('mainWrapper');

        wrappers[cur+1].classList.remove('mainWrapper');
        wrappers[cur+1].classList.add('rightWrapper');
        wrappers[cur+1].children[0].classList.add('rightAct')
        document.getElementById("slideRight").onclick = next;

        if(cur > 0){
            wrappers[cur-1].classList.add('leftWrapper');
            wrappers[cur-1].children[0].classList.add('leftAct');
            document.getElementById("slideLeft").onclick = previous;
        } else {
            document.getElementById("slideLeft").onclick = null;
        }
}

function remove(button) {
    var activityId = button.parentElement.getAttribute("activityId");
    const data = new URLSearchParams();
    data.append('activityId', activityId);
    fetch("http://localhost:8080/a/remove", {
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
        window.location.replace("http://localhost:8080/a/activities");
    }
}

function edit(button){
    var wrappers = document.getElementById("activityList").children;
    var inputs = wrappers[cur].querySelectorAll('input, textarea');
    for(var i = 0; i < inputs.length; i++){
       inputs[i].readOnly = false;
       switch(inputs[i].classList[0]){
         case 'inputName':
            tempName = inputs[i].value;
            break;
         case 'activityDesc':
            tempDescription = inputs[i].value;
            break;
         case 'startDateTime':
            tempStartTime = inputs[i].value;
            break;
         case 'endDateTime':
            tempEndTime = inputs[i].value;
            break;
       }
    }
    var repetition = wrappers[cur].querySelectorAll('div.activityRepetition');
    if(repetition.length != 0){
        editRepetition(repetition[0]);
    }
    wrappers[cur].children[0].querySelectorAll('div.activityButtons')[0].style.display = 'none';
    document.getElementById('editButtons').style.display = 'block';
    editState = true;
}

function editRepetition(repetition) {
    var input = repetition.children[1];
    input.readonly = false;
    tempRDays = input.value;
    var select = repetition.children[2];
    select.disabled = false;
    tempRepetition = select.value;
}

function cancel(){
    if(!editState)
        return;
    var wrappers = document.getElementById("activityList").children;
    var inputs = wrappers[cur].querySelectorAll('input, textarea');
    for(var i = 0; i < inputs.length; i++){
       inputs[i].readOnly = true;
       switch(inputs[i].classList[0]){
         case 'inputName':
            inputs[i].value = tempName;
            break;
         case 'activityDesc':
            inputs[i].value = tempDescription;
            break;
         case 'startDateTime':
            inputs[i].value = tempStartTime;
            break;
         case 'endDateTime':
            inputs[i].value = tempEndTime;
            break;
       }
    }
    var repetition = wrappers[cur].querySelectorAll('div.activityRepetition');
    if(repetition.length != 0){
       cancelRepetition(repetition[0]);
    }
    wrappers[cur].children[0].querySelectorAll("div.activityButtons")[0].style.display = 'flex';
    document.getElementById('editButtons').style.display = 'none';
    editState = false;
}

function cancelRepetition(repetition){
    var input = repetition.children[1];
    var select = repetition.children[2];
    input.readonly = true;
    input.value = tempRDays;
    select.disabled = true;
    select.value = tempRepetition;
    toggleDaysInput(select);
}

function confirm() {
    if(!editState)
        return;
    var wrapper = document.getElementById("activityList").children[cur];
    var activityId = wrapper.querySelectorAll('div.activityButtons')[0].getAttribute("activityId");
    const data = new URLSearchParams();
    data.append('id', activityId);
    var inputs = wrapper.querySelectorAll('input, textarea');
    var repetition = wrapper.querySelectorAll('div.activityRepetition');

    for(var i = 0; i < inputs.length; i++){
        switch(inputs[i].classList[0]){
            case 'inputName':
                data.append('name', inputs[i].value);
                break;
            case 'activityDesc':
                data.append('description', inputs[i].value);
                break;
            case 'startDateTime':
                data.append('startTime', inputs[i].value);
                break;
            case 'endDateTime':
                data.append('endTime', inputs[i].value);
                break;
        }
    }

    if(repetition.length != 0){
        data.append('repetitionDays', repetition[0].children[1].value);
        data.append('repetition', repetition[0].children[2].value);
    }

    editState=false;
    fetch("http://localhost:8080/a/edit", {
        method: "POST",
        body: data
    })
    .then(response => {
        handleResponce(response, 'edit');
        wrapper.children[0].querySelectorAll("div.activityButtons")[0].style.display = 'flex';
        document.getElementById('editButtons').style.display = 'none';
        for(var i = 0; i < inputs.length; i++){
                inputs[i].readOnly = true;
        }
        if(repetition.length != 0){
            var input = repetition[0].children[1];
            var select = repetition[0].children[2];
            input.readonly = true;
            select.disabled = true;
            toggleDaysInput(select);
        }
     })
    .catch(error => {
        window.alert('Error occurred D:');
        editState=true;
    });

}

function checkDates(){
    var startDate = new Date(document.getElementById("startTime").value);
    var endDate = new Date(document.getElementById("endTime").value);
    if(endDate > startDate)
        return true;

    document.getElementById("invalidDate").style.display = 'block';
    return false;
}

function hideError(){
    document.getElementById("invalidDate").style.display = 'none';
}

function previousWeek(){
var today = new Date(document.getElementById("activityList").getAttribute("day"));
    today.setDate(today.getDate() - 7);
    window.location.replace("http://localhost:8080/a/activities/" + today.getFullYear() + '-' + (today.getMonth() + 1).toString().padStart(2, '0') + '-' + today.getDate().toString().padStart(2, '0'));
}

function previousDay(){
    var today = new Date(document.getElementById("activityList").getAttribute("day"));
    today.setDate(today.getDate() - 1);
    window.location.replace("http://localhost:8080/a/activities/" + today.getFullYear() + '-' + (today.getMonth() + 1).toString().padStart(2, '0') + '-' + today.getDate().toString().padStart(2, '0'));
}

function nextDay(){
var today = new Date(document.getElementById("activityList").getAttribute("day"));
    today.setDate(today.getDate() + 1);
    window.location.replace("http://localhost:8080/a/activities/" + today.getFullYear() + '-' + (today.getMonth() + 1).toString().padStart(2, '0') + '-' + today.getDate().toString().padStart(2, '0'));
}

function nextWeek(){
var today = new Date(document.getElementById("activityList").getAttribute("day"));
    today.setDate(today.getDate() + 7);
    window.location.replace("http://localhost:8080/a/activities/" + today.getFullYear() + '-' + (today.getMonth() + 1).toString().padStart(2, '0') + '-' + today.getDate().toString().padStart(2, '0'));
}

function toggleDaysInput(select){
    var value = select.value;
    if(value == "custom")
        select.parentElement.children[1].style.display = 'block';
    else
        select.parentElement.children[1].style.display = 'none';
}