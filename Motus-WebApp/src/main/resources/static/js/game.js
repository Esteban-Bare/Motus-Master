document.addEventListener('DOMContentLoaded', function() {
    const wordDisplay = document.getElementById('wordDisplay');
    const guessForm = document.getElementById('guessForm');
    const guessInput = document.getElementById('guessInput');
    const gameMessage = document.getElementById('gameMessage');
    const newGameButton = document.getElementById('newGameBtn');
    const difficultyElem = document.getElementById('difficulty')
    const wordLengthElem = document.getElementById('wordLength')
    const attemptsElem = document.getElementById('attempts')
    const currentScoreElem = document.getElementById('currentScore')

    let currentWord = null;
    let gameState = {
        attempts: [],
        attemptsLeft: 6,
        maxAttempts: 6,
        currentScore: 0,
        gameOver: false,
        gameWon: false
    }

    initNewGame();
    loadLeaderboard();

    guessForm.addEventListener('submit', function(event) {
        event.preventDefault();
        submitGuess();
    })

    newGameButton.addEventListener('click', function() {
        window.location.reload();
    })

    guessInput.addEventListener('input',function () {
        this.value = this.value.toUpperCase();
    })


    function initNewGame() {
        fetch('/game/new')
            .then(response => response.json())
            .then(data => {
                currentWord = data.word;
                gameState = data.gameState

                updateGameInfo();
                wordDisplay.innerHTML = '';
                createWordDisplay();

                if (data.message) {
                    showMessage(data.message, 'info');
                }

                newGameButton.classList.add('d-none');
                guessForm.classList.remove('d-none');

                guessInput.focus();
                guessInput.value = '';
                guessInput.maxLength = currentWord.length;
            })
            .catch(e => {
                console.error("Error starting new game:", e);
                showMessage("Error starting new game. Please try again.", 'danger');
            })
    }

    function submitGuess() {
        const guess = guessInput.value.trim().toUpperCase()

        if (guess.length !== currentWord.length) {
            showMessage(`Please enter a ${currentWord.length}-letter word.`, 'warning');
            return;
        }

        fetch('/game/guess', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ guess })
        })
        .then(r => r.json())
        .then(d => {
            gameState = d.gameState;

            updateGameInfo();
            createWordDisplay();

            if (d.message) {
                showMessage(d.message, d.gameState.gameOver ? (d.gameState.gameWon ? 'success' : 'danger') : 'info');
            }

            if (d.gameState.gameOver) {
                guessForm.classList.add('d-none');
                newGameButton.classList.remove('d-none');
            } else {
                guessInput.value = '';
                guessInput.focus();
            }
        })
        .catch(e => {
            console.error("Error submitting guess:", e);
            showMessage("Error submitting guess. Please try again.", 'danger');
        })
    }

    function updateGameInfo() {
        const difficulty = currentWord.difficulty;
        difficultyElem.textContent = difficulty === 1 ? 'Easy' : difficulty === 2 ? 'Medium' : 'Hard';
        difficultyElem.className = 'badge';
        difficultyElem.classList.add(`bg-difficulty-${difficulty}`);

        wordLengthElem.textContent = `Word length: ${currentWord.length}`;

        attemptsElem.textContent = `Attempts left: ${gameState.attemptsLeft}/${gameState.maxAttempts}`;

        currentScoreElem.textContent = `Current score: ${gameState.currentScore}`;
    }

    function createWordDisplay() {
        wordDisplay.innerHTML = '';

        // Display attempts
        gameState.attempts.forEach(attempt => {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'word-row';

            attempt.forEach(l => {
                const letterBox = document.createElement('div');
                letterBox.className = `letter-box ${l.status}`;
                letterBox.textContent = l.value;
                rowDiv.appendChild(letterBox);
            });

            wordDisplay.appendChild(rowDiv);
        });

        // Add placeholder row if game is not over
        if (!gameState.gameOver && gameState.attempts.length < gameState.maxAttempts) {
            createWordPlaceholder(currentWord.length);
        }
    }

    function showMessage(message, type) {
        gameMessage.textContent = message;
        gameMessage.className = `alert alert-${type}`;
        gameMessage.classList.remove('d-none');

        if (type !== 'danger' && type !== 'success') {
            setTimeout(() => {
                gameMessage.classList.add('d-none');
            }, 5000);
        }
    }

    function createWordPlaceholder(length) {
        const row = document.createElement('div');
        row.className = 'word-row placeholder-row';

        for (let i = 0; i < length; i++) {
            const cell = document.createElement('div');
            cell.className = 'letter-box empty'; // Changed from letter-cell to letter-box
            row.appendChild(cell);
        }

        wordDisplay.appendChild(row);
    }
})

function loadLeaderboard() {
    const leaderboardDisplay = document.getElementById('leaderboardDisplay');

    console.log("didcall")
    fetch('/game/top-scores')
        .then(response => response.json())
        .then(data => {
            leaderboardDisplay.innerHTML = '';


            console.log("didcall")

            if (data.length === 0) {
                leaderboardDisplay.innerHTML = '<p>No scores yet!</p>';
                return;
            }

            const table = document.createElement('table');
            table.className = 'table table-striped table-sm';

            // Create table header
            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');
            ['Rank', 'Player', 'Score', 'Date'].forEach(headerText => {
                const th = document.createElement('th');
                th.textContent = headerText;
                headerRow.appendChild(th);
            });

            thead.appendChild(headerRow);
            table.appendChild(thead);

            // Create table body
            const tbody = document.createElement('tbody');
            data.forEach((entry, index) => {
                const row = document.createElement('tr');

                const rankCell = document.createElement('td');
                rankCell.textContent = index + 1;

                const playerCell = document.createElement('td');
                playerCell.textContent = entry.username;

                const scoreCell = document.createElement('td');
                scoreCell.textContent = entry.score;

                const dateCell = document.createElement('td');
                dateCell.textContent = new Date(entry.date).toLocaleDateString();

                row.appendChild(rankCell);
                row.appendChild(playerCell);
                row.appendChild(scoreCell);
                row.appendChild(dateCell);

                tbody.appendChild(row);
            });

            table.appendChild(tbody);
            leaderboardDisplay.appendChild(table);
        })
        .catch(error => {
            console.error('Error loading leaderboard:', error);
            leaderboardDisplay.innerHTML = '<div class="alert alert-danger">Error loading leaderboard</div>';
        });
}

