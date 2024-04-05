function toggleEdit(field) {
    var textElement = document.getElementById(field + "Text");
    var inputElement = document.getElementById(field + "Input");
    if (textElement && inputElement) {
        textElement.style.display = "none";
        inputElement.style.display = "inline-block";
        inputElement.value = textElement.innerText;
    }
}

var currentTab = 'user-info';

window.onload = function() {
    document.getElementById('firstNameInput').style.display = 'none';
    document.getElementById('lastNameInput').style.display = 'none';

    var anchor = window.location.hash;
    if (anchor) {
        var tabName = anchor.substring(1); // remove # character
        showTab(tabName);
    }
};

function showTab(tabName) {
    if (currentTab !== tabName) {
        var tabs = document.querySelectorAll('.tabs li');
        tabs.forEach(tab => {
            tab.classList.remove('active')
        });

        var selectedTab = document.querySelector('[data-tab="' + tabName + '"]');
        if (selectedTab) {
            selectedTab.classList.add('active');
        }

        var currentTabContent = document.getElementById(currentTab);
        if (currentTabContent) {
            currentTabContent.style.display = 'none';
        }

        var selectedTabContent = document.getElementById(tabName);
        if (selectedTabContent) {
            selectedTabContent.style.display = 'block';
        }

        currentTab = tabName;
    }
}

function selectBuddy(element) {
    var firstName = element.textContent.split(' ')[0];
    var lastName = element.textContent.split(' ')[1];
    var email = element.getAttribute('data-email');

    var selectedBuddyName = document.getElementById('selected-buddy-name');
    var selectedBuddyEmail = document.getElementById('selected-buddy-email');
    var buddyEmailInput = document.getElementById('buddy-email-input');
    var removeBuddyForm = document.getElementById('remove-buddy-form');

    selectedBuddyName.innerText = firstName + ' ' + lastName;
    selectedBuddyEmail.innerText = email;
    buddyEmailInput.value = email;
    removeBuddyForm.style.display = 'block';
}

function selectBankAccount(element)  {
    var accountType = element.textContent;
    var accountId = element.getAttribute('data-id');

    fetch('/bank-account/' + accountId)
        .then(response => response.json())
        .then(data => {
            var selectedAccountType = document.getElementById('selected-account-type');
            var selectedBankName = document.getElementById('selected-bank-name');
            var selectedAccountNumber = document.getElementById('selected-account-number');
            var selectedIban = document.getElementById('selected-iban');
            var selectedBic = document.getElementById('selected-bic');

            selectedAccountType.innerText = "Type: " + data.accountType;
            selectedBankName.innerText = "Bank name: " + data.bankName;
            selectedAccountNumber.innerText = "Account number: " + data.accountNumber;
            selectedIban.innerText = "IBAN: " + data.iban;
            selectedBic.innerText = "BIC: " + data.bic;
        })
        .catch(error => console.error('Error when loading bank account details :', error));
}