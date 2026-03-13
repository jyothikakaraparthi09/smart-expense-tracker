let currentPage = 0;
const pageSize = 5;

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

function addExpense(){
    const title = document.getElementById("title").value;
    const amount = document.getElementById("amount").value;
    const category = document.getElementById("category").value;
    const date = document.getElementById("date").value;

    fetch("/expenses",{
        method: "POST",
        headers:{
            "Content-Type": "application/json",
            "Authorization":"Bearer "+localStorage.getItem("token")
        },
        body:JSON.stringify({
            title: title,
            amount: amount,
            category: category,
            date: date})
        })
    .then(res=>res.json())
    .then(()=>loadExpenses())
}

function loadExpenses(){
    fetch(`/expenses?page=${currentPage}&size=${pageSize}`,{
        method: "GET",
        headers: {
            "Authorization": "Bearer "+localStorage.getItem("token")
        }
    }).then(res=>res.json())
        .then(data=>{
            const table=document.getElementById("expenseTable");
            table.innerHTML="";
            data.content.forEach(expense => {
                table.innerHTML+=`<tr><td>${expense.title}</td><td>${expense.amount}</td><td>${expense.category}</td><td>${expense.date}</td></tr>`
            })
            document.getElementById("pagenumber").innerText=currentPage+1;
            /* Disable prev & next buttons */
            document.getElementById("prevBtn").disabled = data.first;
            document.getElementById("nextBtn").disabled = data.last;
        })
}

function loadExpensesByCategory(){
    currentPage = 0;
    const category = document.getElementById("category1").value;
    fetch(`/expenses/category/${category}?page=${currentPage}&size=${pageSize}`,{
        method: "GET",
        headers: {
            "Authorization": "Bearer "+localStorage.getItem("token")
        }
    }).then(res=>res.json())
        .then(data=>{
            const table=document.getElementById("expenseTable");
            table.innerHTML="";
            data.content.forEach(expense=>{
                table.innerHTML+=`<tr><td>${expense.title}</td><td>${expense.amount}</td><td>${expense.category}</td><td>${expense.date}</td></tr>`
            })
            document.getElementById("pagenumber").innerText=catCurrentPage+1;
            /* Disable prev & next buttons */
            document.getElementById("prevBtn").disabled = data.first;
            document.getElementById("nextBtn").disabled = data.last;
        })
}

function nextPage(){
    currentPage++;
    loadExpenses();
}

function prevPage(){
    if(currentPage > 0){
        currentPage--;
    }
    loadExpenses();
}
function logout(){
    localStorage.removeItem("token");
    window.location="login.html";
}

loadExpenses();