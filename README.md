# KinOverflow
A demo app demonstrating how stackoverflow could possibly integrate tipping with KIN

## Disclaimer
This demo app was written during a 3 day hackathon. Therefore we have limited the app to the simplest 
UI & functionality we could provide within this timeline and made a lot of allowances in terms of 
functionality/ui presentation and code quality.


##About
<ul>
<li>KinOverflow aims to demonstrate how real monetary value can be given to users contributing in stackoverflow using
 KIN. We thought of many ways we could do this, including setting monetary bounties to questions but decided to demonstrate 
 the simplest of all: Allow users to tip each other by giving KIN to contributors that asked a great question or 
 gave a valuable answer to an existing question.
<li>The app works with ethereum ropsten (testnet) blockchain and when it is started the first time it will create a 
new wallet with a hardcoded passphrase.
<li>To start-off KIN usage we thought it would be great to give each existing contributor a KIN award 
the amount of which would ideally be derived from the existing badges and reputation that the contributor has 
already acquired in stackoverflow. However for the hackathon we award every user logging into the app 
with 10000 KIN, using the testnet fountain provided by Kik. 
<li>The app works with real questions and answers from stackoverflow using the stackexchange api.
<li>We haven't implemented a login/authentication screen. By default the demo will display as if 'yosriz' 
has logged in and the 4th question in the list will display as if it was written by Berry Ventura Lev even though
she hasn't written that question!! 
This can be configured by opening the SettingsActivity (from SettingsActivity launch icon).
</ul>