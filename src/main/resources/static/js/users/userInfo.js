function changePhoto(){
    var input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/png, image/jpeg';

    input.onchange = e => {
       const selectedImage = e.target.files[0];
       if (!selectedImage){
            window.alert('Error loading file D:');
            return;
       }

       const formData = new FormData();
       formData.append("image", selectedImage);

        fetch("http://localhost:8080/updateProfilePicture", {
        method: "POST",
        body: formData
        })
        .then(response => handleResponce(response, selectedImage))
        .catch(error => {
        window.alert('Error loading file D:');
        console.error("Error:", error);
        });
    }

    input.click();
}

function handleResponce(res, uImage) {
    if(!res.ok){
        throw res.status;
        return;
    }
    const image = document.getElementById("profile-pic");
    image.src = URL.createObjectURL(uImage);
}

function logout(){
    fetch("http://localhost:8080/logout", {
    method: "POST"
    });
    window.location.href = 'http://localhost:8080/login';
}