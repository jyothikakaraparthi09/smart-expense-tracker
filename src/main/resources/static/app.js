async function loadConfig(){
    const response = await fetch("/config");
    const data = await response.json();
    BASE_URL = data.baseURL;
    localStorage.setItem("BASE_URL",BASE_URL);
}

loadConfig();

async function register(){
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/auth/register",{
        method: "POST",
        headers: {"Content-Type":"application/json"},
        body: JSON.stringify({
            username: username,
            password: password
        })
    });

    if(response.ok){
        document.getElementById("message").innerText = "User Registered Successfully";
    }else{
        document.getElementById("message").innerText = "Unable to register user";
    }
}





async function login(){
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/auth/login",{
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