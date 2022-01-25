# Stock Management Tool

### Setting up local persistent H2 database for debugging

Note under application-dev.yml the section that looks as follows:

```
spring:
  datasource:
    url: jdbc:h2:./data/stock_data;AUTO_SERVER=TRUE
    username: stock_management
    password: password
    driver-class-name: org.h2.Driver
```

The url that needs to be copied includes the `;AUTO_SERVER=TRUE`.
Add a database connection for an H2 database, then select URL Only
and copy + paste the *entire* url `jdbc:h2:./data/stock_data;AUTO_SERVER=TRUE`.
This may be obvious to some, but I spend 1-2 hours rediscovering
this answer every time I open a spring boot project, I swear.

This creates a persistent h2 database in the ./data directory
(relative to project root), and allows you to view/monitor

### Setting up connection to remote MySQL server (personal note)

check [this](https://youtrack.jetbrains.com/issue/DBE-13313?_ga=2.21066931.903409886.1643045031-387666555.1642102571)
solution out for connecting to remote server if you're having an error that looks like
this:

``
The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
``

Basically, when connecting to the database through IntelliJ, 
do the normal thing for MySQL, then go to advanced, and change
the following two settings:
1. `enabledTLSProtocols` - `TLSv1,TLSv1.1,TLSv1.2,TLSv1.3`
2. `useSSL` - `FALSE`

