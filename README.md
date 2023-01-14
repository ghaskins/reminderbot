# Reminder Bot for Slack

This project implements a Bot for Slack that adds a /remindme command.  This command allows you to schedule a future reminder posted back to yourself at a specific time as a Direct Message (DM).

## Motivation

This bot is of little practical use, and the implementation is probably overkill since Slack already supports a [Scheduled Message](https://api.slack.com/messaging/scheduling) API.  The Slack documentation reads:

> If you want to do things the hard way, your app could implement state storage and job scheduling to send this message at the right time ...

We **do** want to do things the hard way here because the purpose is to demonstrate [Temporal](https://temporal.io/) and the [temporal-clojure-sdk](https://github.com/manetu/temporal-clojure-sdk).  What I hope you take away from this is that this remains easy through the elegance and power of Temporal and Clojure, despite the warning from the Slack documentation.  This power will matter more when you build applications of consequence rather than this silly toy.

## Installation

You will need to [install a Slack App](https://api.slack.com/authentication/basics) with a Slash Command for /remindme, and OAuth scopes for

- chat:write
- commands

Collect the App Token and Bot Token for later use

## Running

### Temporal

You will need an available Temporal service.  A convenient way to get this for testing is via [temporalite](https://github.com/temporalio/temporalite)

```shell
git clone https://github.com/temporalio/temporalite.git
cd temporalite
go build -o dist/temporalite ./cmd/temporalite
./dist/temporalite start --namespace default --ephemeral
```

### ReminderBot

Set up environment variables for the tokens generated in the previous steps

```shell
export APP_TOKEN=xxx
export BOT_TOKEN=yyy
```

Run the application

```shell
lein run
```

## Usage from with Slack

Once the bot is active in your workspace, you will find a new command '/remindme' accessible from any channel.  You may invoke this command with a deferred message and time duration, and the bot will dutifully post the message back to you at the specified time.

Example:

```shell
/remindme "take out the trash" in 20 minutes
```

The parsing grammar is limited and likely fragile.  At a high level, it needs a basic structure of a quoted phrase, followed by "in" and a number, and a unit that is one of "seconds", "minutes", "hours", or "days".
