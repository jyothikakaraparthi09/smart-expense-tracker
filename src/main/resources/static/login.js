async function login(){
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    await fetch("/auth/login",{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
        .then(response => response.text())
        .then(token => {
            console.log("JWT Token: "+token);
            localStorage.setItem("token",token);
            window.location.href = "dashboard.html";
        });
}

async function register(){
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const error = document.getElementById("error").enabled;

    if(!error) {
        const response = await fetch("/auth/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (response.ok) {
            document.getElementById("message").innerText = "User Registered Successfully";
            document.getElementById("username").innerText = "";
            document.getElementById("password").innerText = "";
        } else {
            document.getElementById("message").innerText = "Unable to register user";
        }
    }
}

function checkPwd(){
    const password = document.getElementById("password").value;
    const cPassword = document.getElementById("cPassword").value;

    if(password !== cPassword){
        document.getElementById("error").enabled = true;
        document.getElementById("error").innerText = "Password should match!";
    }else {
        document.getElementById("error").enabled = false;
        document.getElementById("error").innerText = "";
    }

}
