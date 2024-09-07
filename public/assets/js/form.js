const emailRegExp = /^[A-Za-z0-9._+\-']+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/;
const button = document.getElementById("btn")
const emailField = document.getElementById("email-field")
const suggestionField = document.getElementById("suggestion-field")

emailField.addEventListener("focus", ()=> {
    suggestionField.style.display = "block"
})
button.addEventListener("click",() => {
    let _email = emailField.value.toString();
    if (!emailRegExp.test(_email)) {
        alert("Email is not valid.");
        return;
    }
    let _suggestion = suggestionField.value.toString();
    if (_suggestion.length === 0) _suggestion = null;

    button.disabled = true;
    button.innerText = "Loading..."

    fetch('https://api.studentintellect.co.za/subscribe', {
        mode: 'cors',
        method: 'POST',
        body: JSON.stringify({
            email: _email,
            suggestion: _suggestion
        }),
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'text/plain'
            // 'Authorization': 'Bearer ' + token
        }
    }).then(async (response) => {
        let message = await response.text()
        if (response.ok) {
            console.log(message);
            alert(message);
            document.getElementById('form').hidden = true;
        } else {
            console.error(message);
            let msg = message.split(":")
            alert(msg.length === 1 ? msg[0] : msg[1]);
            button.disabled = false;
            button.innerText = "Notify me!"
        }
    });
});