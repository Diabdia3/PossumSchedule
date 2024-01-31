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

function toggleDaysInput(select){
    var value = select.value;
    if(value == "custom")
        document.getElementById("repetitionDaysWrapper").style.display = 'inline';
    else
        document.getElementById("repetitionDaysWrapper").style.display = 'none';
}