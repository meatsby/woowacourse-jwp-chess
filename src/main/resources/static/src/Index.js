let first = '';
let second = '';

function clicked(id) {
    setVariable(id);
    changeClickedBackground(id);
    checkSendToServer();
}

function setVariable(id) {
    if (first !== '') {
        second = id;
        return;
    }
    first = id;
}

function changeClickedBackground(id) {
    let elementById = document.getElementById(id);
    elementById.style.backgroundColor = "#ff0";
}

function checkSendToServer() {
    if (first !== '' && second !== '') {
        sendToServer(first, second);
        first = '';
        second = '';
    }
}

function checkStatus() {
    let element = document.getElementById("roomId");

    fetch(`/room/${element.value}/status`, {
        method: "GET",
        headers: {
            "Content-Type": "text/plain",
        }
    }).then((response) => {
        response.json().then(data => {
            document.getElementById("statusResult")
                .innerHTML = "검은 말 : " + data.score.BLACK + "<br>"
                + "흰말 : " + data.score.WHITE + "<br>"
                + data.winner;
        });
    });
}

function sendToServer(first, second) {
    let element = document.getElementById("roomId");
    fetch(`/room/${element.value}/move`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({start: `${first}`, target: `${second}`})
    }).then((response) => {
            console.log(response.status);
            if (response.status === 400) {
                throw response;
                return;
            }
            response.json().then(data => {
                console.log(data.finished);
                if (data.finished === true) {
                    alert("게임이 종료되었습니다.");
                    document.location.href = `/`
                    return;
                }
                location.reload();
            });
        }
    ).catch(err => {
        err.text().then(msg => {
            alert(msg);
            location.reload();
        })
    });
}

