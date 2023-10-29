# PostgreSQL Install And Basic Usage

## Mac

### Install

Way 1: 

```shell
/bin/bash -c “$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)”
```

Way 2:

```shell
brew install postgresql
```

### Starting and Stopping the Postgres Service

```shell
# starting
brew services start postgresql
# stopping
brew services stop postgresql
# info
brew services info postgresql
```

### Configure the Postgres Data Server

Log into the postgres service.

```shell
psql postgres
```

Create a root user that will have administrator privileges to the database server.

```sql
-- create user: sunday
CREATE ROLE sunday WITH LOGIN PASSWORD 'password';
ALTER ROLE sunday CREATEDB;
```

Once the new user is created, you can also start using the credentials and log in with the new user's credentials.
You can use the following command to log in with the new user's credentials. First, we need to quit the current session and then reconnect with the new user's credentials.

```shell
# quit current session
\q
# log in new user
psql postgres -U sunday
```

### Basic Usage

Create a new database:

```sql
CREATE DATABASE smile;

-- list databases;
\l
```

