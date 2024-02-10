function resend() {
        fetch("http://localhost:8080/resendLink", {
        method: "POST"
        })
        .then(response => handleResponse(response))
        .catch(error => {
        window.alert('Error loading file D:');
        console.error("Error:", error);
        });
}

function handleResponse(res, uImage) {
    var p = document.getElementById("resendP");
    if(!res.ok){
        throw res.status;
        p.innerText = "We were unable to resend your confirmation link, our apolocheese.";
        return;
    }
    p.innerText = "New confirmation link  was sent to your email.";
}