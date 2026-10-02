# React + TypeScript + Vite

This project is based on a homework I received for a job interview some time ago.
I kept developing on it as a way to increase my skills and try new things.

## Backend
Springboot application. The basic skeleton was built on the following requirements:

### Sample Activity: Digital Cliche Piggy Bank

#### Objective:
* Develop a digital “Cliche piggy bank” as an MVP

#### Background:
* A “cliché piggy bank” is, in its standard physical form, a hollow container—often shaped like a pig—into which anyone
caught using a cliché must drop a fine.
* To encourage greater self-discipline among colleagues regarding the use of “empty” phrases —and simply as a bit of shared fun— such a cliché piggy bank was recently introduced.
* However, it is located in the office, making its use impractical during periods of working from home.
* To continue penalizing the use of clichés even under these circumstances, a digital solution is therefore needed.

#### Requirement Definition Method
* The requirements are formulated as so-called user stories.
* They therefore follow this structure: First, the role or perspective of the person defining the requirement is specified, followed by a description of the actual requirement, and finally the objective behind the requirement. This is supplemented by acceptance criteria, which are to be understood as acceptance criteria and are therefore either presented during handover or tested by the client to verify that the respective requirement has been met.
* The requirements are prioritized and thus also represent the possible stages of an MVP:
    * Stage 1: Basic Functions
    * Stage 2: Role and Permission Management, Part 1
    * Stage 3: Role and Permission Management, Part 2 (You will need to decide for yourself whether Part 2 truly increases complexity and should therefore be developed in a subsequent phase)

After each level, a handover takes place, including a presentation and/or testing of the acceptance criteria.
At least Level 1 must be completed. Whether Levels 2 and 3 will also be implemented will be decided based on the remaining time available.

| Priority  	| As a...  	| I want to...  	| in order to...  	| Acceptance criterion  	|
|---	|---	|---	|---	|---	|
| 1  	| As a piggibank user  	| penalize other users  	| condone the use of a hollow phrase  	| * Sebastian gives Alex a standard penalty (1€), and the corresponding amount is then credited to Alex's account.  	|
| 1  	| As a piggibank user  	| have an account into which penalty payments can be deposited  	| be able to accumulate multiple sanctions  	|   	|
| 1  	| As a piggibank user  	| reset another user's balance to 0,-  	| document the physical payment of outstanding balances  	|   	|
| 1  	| As a piggibank user  	| enter the reason or the penalized phrase into a free-text field  	| record the reason so that I'll have an overview later.  	|   	|
| 1  	| As a piggibank user  	| have an overview of all free-text reasons  	| have a reminder of the funniest moments  	|   	|
| 1  	| As a piggibank user  	| that there is an administrator role held by an external person  	| have a neutral authority  	|   	|
| 2  	| As administrator of the piggibank  	| be able to add people  	| be able to expand the user base as needed  	|   	|
| 2  	| As administrator of the piggibank  	| be able to remove or deactivate people  	| be able to reduce the user base if necessary  	|   	|
| 2  	| As administrator of the piggibank  	| be able to remove a person only once the outstanding balance is 0,-  	| ensure that the debts have been paid off first  	|   	|
| 3  	| As administrator of the piggibank  	| be able to set the balances in a user account to 0.00  	| to increase security compared to the simple solution described above  	|   	|
| 3  	| As a piggibank user  	| impose penalties of varying amounts on other users  	| have more flexibility in determining the severity of penalties  	|   	|
| 3  	| As a piggibank user  	| vote on the list of reasons  	| be able to choose a “Best Of”  	|   	|

## Frontend
Created with React. A landing page is provided, that guides to three dedicated pages for:
* Users
* Accounts
* Sanctions

The featuers are added progressively. Whenever the frontend suggests a modification of the frontend, this is also adapted.