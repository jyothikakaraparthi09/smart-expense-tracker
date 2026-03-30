let currentPage = 0;
const pageSize = 5;
let categoryPage = false;

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

    fetch(`/expenses/?page=${currentPage}&size=${pageSize}`,{
        method: "GET",
        headers: {
            "Authorization": "Bearer "+localStorage.getItem("token")
        }
    }).then(res=>res.json())
        .then(data=>{
            const table=document.getElementById("expenseTable");
            table.innerHTML="";
            console.log(data);
            data.content.forEach(expense => {
                table.innerHTML+=`<tr><td>${expense.title}</td><td>${expense.amount}</td><td>${expense.category}</td><td>${expense.date}</td><td><button onclick="deleteExpense(${expense.id})">Delete</button> </td></tr>`
            })
            document.getElementById("pagenumber").innerText=data.page+1;
            /* Disable prev & next buttons */
            document.getElementById("prevBtn").disabled = data.first;
            document.getElementById("nextBtn").disabled = data.last;
        });
}

function loadExpensesByCategory(){
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
            document.getElementById("pagenumber").innerText=data.page+1;
            /* Disable prev & next buttons */
            document.getElementById("prevBtn").disabled = data.first;
            document.getElementById("nextBtn").disabled = data.last;
        })
}

function nextPage(){
        currentPage++;
        if(categoryPage)
            loadExpensesByCategory();
        else
            loadExpenses();
}

function prevPage(){
    currentPage--;
    if(categoryPage)
        loadExpensesByCategory();
    else
        loadExpenses();
}
function logout(){
    localStorage.removeItem("token");
    window.location="login.html";
}

function totalExpenseAmount(){
    fetch("/expenses/total-spent",{
        method: "GET",
        headers: {
            "Authorization": "Bearer "+localStorage.getItem("token")
        }
    }).then(response => response.json())
        .then(data => {
           document.getElementById("totalAmount").innerText = data;
        }).catch(error => console.error("Error: ", error));

}

function topSpentCategory(){
    fetch("/expenses/top-category",{
        method: "GET",
        headers: {
            "Authorization": "Bearer "+localStorage.getItem("token")
        }
    }).then(response => response.json())
        .then(data => {
            Object.entries(data).forEach(([category,amount]) =>{
                document.getElementById("topCategory").innerText = `${category}  with amount: ${amount}`;
            })
        });
}

function loadCategoryChart(){
    fetch("/expenses/category-summary",{
        headers:{
            "Authorization":"Bearer "+localStorage.getItem("token")
        }
    })
        .then(res => res.json())
        .then(data => {
            const labels = Object.keys(data);
            const values = Object.values(data);

            renderPieChart(labels,values);
        });
}

function renderPieChart(labels,values){
    const ctx = document.getElementById('categoryChart').getContext('2d');

    new Chart(ctx, {
        type: 'pie',
        data:{
            labels: labels,
            datasets: [{
                label: 'Expenses',
                data: values,
                backgroundColor:['#FF6384','#36A2EB','#FFCE56','#4BC0C0C0','#9966F']
            }]
        }
    });
}

function loadBarChart(){
    const currDate = new Date();
    fetch(`/expenses/monthly-summary/${currDate.getFullYear()}/${currDate.getMonth()+1}`,{
        headers:{
            "Authorization":"Bearer "+localStorage.getItem("token")
        }
    })
        .then(res => res.json())
        .then(data => {
            const labels = Object.keys(data);
            const values = Object.values(data);

            renderBarChart(labels,values);
        });
}

function renderBarChart(labels,values){
    const ctx = document.getElementById("monthlyChart").getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'This Month Expenses',
                data: values
            }]
        }
    });
}

function findByCategory(){
    currentPage = 0;
    categoryPage = true;
    loadExpensesByCategory();
}

function findAllExpenses(){
    currentPage = 0;
    categoryPage = false;
    loadExpenses();
}

function deleteExpense(id){
    fetch(`/expenses/${id}`,{
        method: "DELETE",
        headers:{
            "Authorization":"Bearer "+localStorage.getItem("token")
        },
    }).then(res=> {document.getElementById("delMessage").innerText = "Expense deleted successfully";
                            location.reload();});

}

window.onload = function (){
    categoryPage = false;
    loadExpenses();
    totalExpenseAmount();
    topSpentCategory();
    loadCategoryChart();
    loadBarChart();
}
