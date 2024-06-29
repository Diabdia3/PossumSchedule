var cur = 0;

window.onload = function(){
    var list = document.getElementById("activityList");
    var current = list.getAttribute("current");
    cur = Number(current);
    var wrappers = list.children;
    wrappers[current].classList.add('mainWrapper');
    if(current > 0){
        wrappers[cur-1].classList.add('leftWrapper');
        wrappers[cur-1].children[0].classList.add('leftAct');
        document.getElementById("slideLeft").onclick = previous;
    }
    if(current < wrappers.length-2){
        setTimeout(function(){
            wrappers[cur+1].classList.add('rightWrapper');
            wrappers[cur+1].children[0].classList.add('rightAct');
            document.getElementById("slideRight").onclick = next;
        }, 3500);
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
    window.location.replace("http://localhost:8080/a/activities");
}

function edit() {
    var wrapper = document.getElementById("activityList").children[cur];
    var activityId = wrapper.querySelectorAll('div.activityButtons')[0].getAttribute("activityId");
    window.location.replace("http://localhost:8080/a/edit/" + activityId);
}

function hideError(){
    document.getElementById("invalidDate").style.display = 'none';
}
