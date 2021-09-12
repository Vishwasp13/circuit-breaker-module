# Circuit Breaker Module
This is route based connector which provides two routes
- Main Route
- Fallback Route
The Main route is the happy path scenario when there are no errors, the fallback route is used when the circuit is open.

The Circuit Breaker has the following three states:
- CLOSED
	- This is the default state
	- In this state the event processors in the Main route chain gets executed
	- Once the configured error has encountered, the state changes to error
- ERROR
	- This state is set when first error defined in the error expression occurs
	- In this state the event processors in the Main route chain gets executed
	- A scheduled task is triggered which would kick in action after the defined error threshold period, post this the error ratio will be compared with the specified error threshold percentage, if the error ratio is equal or higher than the specified threshold percentage, the state changes to OPEN, else it switches back to CLOSED
- OPEN
	- This state is set when the circuit is tripped (threshold limit is attained or exceeded), this state can be attained only after ERROR state
	- In this state the event processors in the Fallback route get executed
	- A scheduled task is triggered which would kick in action after the defined open duration expires, post this the state will be switched back to CLOSED
	
## Circuit Breaker State Transition
![Image of Circuit Breaker State Transition](https://github.com/Vishwasp13/circuit-breaker-module/blob/main/images/cb-state-transition.png)


## Installation of Circuit Breaker Module
This section describes the installation process for this mvp connector in order to use in Anypoint Studio. 

### Step 1 - Download the Circuit Breaker Module
- Download Repository
- Extract the repository

### Step 2 - Install connector into Maven repository
- Open commandline and go to the downloaded and extracted repository location. 
- Perform "mvn install" 
- Connector should be installed successfully

### Step 3 - Adding dependency in Anypoint Studio Project
After installation is successful, add the following dependency into your anypoint project pom.xml:

		<dependency>
			<groupId>com.resiliency.connectors</groupId>
			<artifactId>circuit-breaker-connector</artifactId>
			<version>1.0.0</version>
			<classifier>mule-plugin</classifier>			
		</dependency>

Once added, the module should be reflected in the Mule Pallete
![Image of Circuit Breaker MuleSoft Connector Pallete](https://github.com/Vishwasp13/circuit-breaker-module/blob/main/images/mule-pallete.png)

### Step 4 - Create Circuit Breake Configuration
Before you get started, make sure to configure the circuit breaker config to initialize the hazelcast cluster. 
- Member IP Address
	- IP Address of the other nodes on which the same app is hosted
- Host IP Address
	- IP Address of the node on which the mule app is hosted
- Cluster Port
	- Port on which hazelcast has to be initialized

![Image of Circuit Breaker Connector Config](https://github.com/Vishwasp13/circuit-breaker-module/blob/main/images/cb-config.png)

Now you are all set to use the Circuit Breaker.


## Flow Example with circuit breaker operations
![Image of CB in flow](https://github.com/Vishwasp13/circuit-breaker-module/blob/main/images/cb-in-flow.png)

## CB Route configuration
- Circuit Breaker ID
	- Unique ID for the circuit breaker usually the app name, default value is #[app.name]
- Threshold Percentage
	- Percentage of error which when reached or exceeded post threshold period, would make the circuit trip i.e move to open state
- Threshold Period
	- ISO-8601 duration format time, it denotes for what duration the error has to be monitored and counted from the time when first error occurs for circuit breaker to trip
- Open Duration
	- ISO-8601 duration format time, it denotes how long the circuit should be open
		
![Image of CB route flow](https://github.com/Vishwasp13/circuit-breaker-module/blob/main/images/cb-route-config.png)

## Caution
Ensure that only circuit breaker config is used per application

## Youtube links
- Part 1 : https://youtu.be/tPLu6yW46Nk  
- Part 2 : https://youtu.be/sPRCcMZX4Qk
- Part 3 : https://youtu.be/SmCpAgThqSM
- Part 4 : https://youtu.be/jXQqUOWb8Bg
- Part 5 : https://youtu.be/v-l9943qMmY
	
## Author
- Vishwassingh Pawar