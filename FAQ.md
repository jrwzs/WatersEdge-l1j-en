# Frequently Asked Questions #

## Introduction ##

This document is an in-progress collection of information about l1j-en.  If you know something that can be added here, let us know.

## What is l1j-en? ##

l1j-en is a server emulator for the fantasy MMORPG Lineage 1.

The US servers for Lineage 1 served most of the rest of the world outside of Asia (though many Asians still played on it).  On June 29, 2011, NCSoft shut down the US Lineage 1 servers, probably due to lacking profits from its dwindling player base.  The predecessor to l1j-en, called LinDC, was started prior to this in 2007.  This project continues its attempt to faithfully recreate the gameplay experience from these servers.  This codebase was made open source in an attempt to draw upon the domain knowledge of the US server player base and, of course, to enable others to relive the L1 experience that would otherwise be lost.

## How does l1j-en compare to other L1J variants? ##

l1j-en is the only active English language Lineage emulator, and one of only two open source ones. We also aggregate all of the features and bugfixes from Asian L1J efforts as well as add many of our own. The result is arguably the most stable Lineage 1 emulator available and servers have been able to run wipe-free since its inception (something not possible until l1j-en). Stability is also greatly increased due to no longer needing to follow a moving target.

As far as MMORPG server emulators go, most projects are fairly buggy, only support a small percent of the features of their official counterparts, or don't work at all. l1j-en is one of the few emulators where this mostly isn't the case, and is on par with efforts such as RunUO and L2J. Many hundreds of players actively play on servers using l1j-en or forks of its codebase.  It has also served as an upstream source of many bugfixes that have eventually propogated outward (though the majority are still exclusive to l1j-en and direct forks).

## I just wanna play Lineage! Where do I go? ##

Choose from one of the many servers currently running l1j-en (or even another version of L1J). These servers come and go regularly, and it's difficult to keep track of them.  Here's a few to get you started, but Google searches will probably turn up more current results.

[Lineage Resurrection](http://zelgo.net/) - The new Official l1j-en server.  Focuses on long-term stability.

[Satan's Den](http://www.satansden.enjin.com) - A new server for Theistic Satanists that targets live-like rates.

[Lineage DC](http://www.lineagedc.com/) - Previously the largest Lineage 1 private server. Long running with moderate rates and live-like gameplay.  Currently down.

[Westknights Lineage 1](http://www.westknights.com/) - Mid rate English server.  Extended l1j-en with 3.58 (I think).

[Frisky Cow](http://friskycow.co.cc/) - Oldest L1 server, recently switched to l1j-en. Includes some of the latest features from Asian L1J teams.

[Forgotten Gamers](http://forgottengamers.ucoz.com/) - Another long-running server.

[Risen Server](http://risenserver.com/news.php) - Fast leveling and lots of custom items.

[Dlirk's Private Server](http://www.dlirk.com/) - Semi-high rate with a fair amount of regular users.

[EpicL1](http://epicl1.ucoz.com/) - A primarily Spanish language server.

[Linpride1337](http://linpride1337.ucoz.com/) - Mid rate server.

[Lineage Heroes](http://lineagedj.ucoz.com/) - Another Spanish language server.

[Lineage Evolution](http://www.lineage-evolution.es/) - Yet another mainly Spanish language server.

[Lin Excalibur](http://linexcalibur.ucoz.com/) - Currently down for rework.

If you're running an l1j-en based server, let us know and we'll add it here.

## What happened to the LinDC emulation project? ##

This codebase is a fork of the LinDC codebase from its [revision 894](https://code.google.com/p/l1j-en/source/detail?r=894). The reason this new project was created was mainly: 1.) to implement a long planned name change that wasn't specific to one private server, 2.) to address some of the dev team's internal disagreements on project goals, and 3.) to lose some of the baggage associated with the LinDC codebase going down what we feel was the wrong path.

l1j-en was chosen as a name because this project is in fact an L1J port. The "En" part of it denotes the English language aspect of this server. We considered L1J-US, since we do use the US client, but since we have no intention of implementing the US servers' LoA ruleset, this would have been a misnomer.

## What are the goals of l1j-en? ##

In short, the primary goal of l1j-en is to create the most stable English language Lineage Server Emulator currently possible. The specific goals of the project are as follows, in descending order of priority:

1. Stability: The server should foremost be as stable as possible. By stability, we mean both that the server should be able to run as long as possible unattended and also that the gameplay should be as free of fundamental flaws as possible. The latest feature doesn't do a public server any good if it introduces dozens of bugs in the process or has the potential to unbalance a server with an active playerbase.

2. Fixing bugs: Non-functional or improperly functioning aspects of existing features takes precedence over adding new features. Bug fixing itself is prioritized by the effect it has on gameplay.

3. Translation: Since this server is the only actively developed English language port of L1J, we'll do our best to translate all text content to English. This task is already completed for all existing aspects of the server.

4. New Features: We do want all of the latest new features worked on by the other various L1J projects. However, we take a more conservative approach with new features than other L1J ports, as they tend to be bug-prone when first added. We also implement missing features ourselves as developer time permits.

5. Balance adjustments: The Asian L1J efforts largely target private servers that run at very high rates (i.e. the kind of server where everyone is level 80+ in a few days). While this is fine for some quick fun, we also want to support the ability to run a long-term, if not permanent, public server. This requires a lot of attention to droprates, spawn, and minor features players don't care about on a temporary, high rate server.

6. Server enhancements: Increasing the ease by which server owners can administrate their servers is a final goal. This includes custom settings, support for multiple OSes, GM/Monitor commands, stored procedures, and various other administrative functions. We also maintain an upgrade path for in-use databases.

## What are NOT the goals of l1j-en? ##

There are several things that we could do, but won't for what we feel are valid reasons. A few of the major things we will not do are:

1. Refactoring the codebase: We are well aware of the fact that L1J is rife with lame technologies and very uninspired design. L1J is a great example of how NOT to design a large piece of software. While we share in the many complaints anyone with experience in software development will have within a few minutes of examining its architecture (or lack thereof), we will do as little refactoring as possible simply because we want to leverage the work being done by other L1J efforts. Diverging too far from their structure complicates the merge process, and while it would still be possible to make use of their additions, the English-speaking Lineage community lacks the talented manpower to re-implement every one of these individually.

2. Support: While members of the development team do actively assist in the efforts of some of the public servers using the codebase, what we won't do is provide personal support for every person that downloads the code. The reason for this is simply that dev team members don't have time to provide individual walkthroughs of the setup process and all of the aspects of server administration. A setup guide is provided on this site, and support can be had from other admins and users on the LineageDC forum, however. We'll also do our best to make the various steps of the processes as self-explanatory as possible, but don't guarantee that it will be accessible to those who lack basic computer skills.

3. Custom features: Many private servers provide a Lineage experience that has modified rules from the live servers. While we're happy to add any custom feature if it can be toggled with a configurable setting, what we won't do is modify the basic game mechanics of Lineage itself to satisfy one server's vision of what Lineage should be. Admins who want to do this are welcome to make their own private modifications, of course.

4. Monetization: Some private servers seem to exist as enterprises soley to make money (i.e. running a cash shop).  Generally, the l1j-en team frowns upon this practice, preferring the donations model if an admin needs help paying VPS bills.  Know that all L1J variants actually don't require much in the way of hardware or bandwidth to operate (though it does benefit from low latency).  For player count under a couple dozen, most people can probably get away even hosting from a consumer ISP connection.

## I want to run the server, but am getting some error. What should I do? ##

Feel free to post on the [l1j-en user group](http://groups.google.com/group/l1j-en) forum.  Alternatively, join #l1j on Freenode and if anyone's paying attention, they may be able to answer a question.

Lastly, if you're still having problems, and are either unable to explain it properly or get an answer from other users, you can always take a look at the code itself. Experience with most any higher-level programming language should be adequate to get an understanding of what should be occurring during server initialization. If the error is occurring at an earlier step, such as compilation or getting the server to start, documentation on the tool(s) you are using will probably provide the answer to what you're doing wrong.

If you've tried all of the available methods and still can't get the server running, then why not take advantage of the fact that several publicly available servers (some of which are listed above) and who have already done all the work for you?.

## How do I report bugs? ##

There's a Issues tab on the site that is open to the general public to add bug reports. Create a new issue and fill out the form fields. Please add as much detail as possible, so that whoever looks at it can recreate the bug in a test environment. Recreating the bug is almost always a necessary part of finding the bad code and fixing it.

You can also post issues on the GitHub fork of l1j-en, as most of the active developers read that more often.

## How do I help this project? ##

We do want your help! As an open source project, you can assist the further progress of Lineage 1 emulation by contributing to this project. Feel free to download the code and get familiar with it. Then, if you identify something needing attention or can address something on the issues list, let us know. Continue reading for full details.

## How do I submit a patch? ##

If you're already on the development team, just check in via SVN. Otherwise, email your patches to one of the project owners, and if it meets at least minimal quality standards, we'll gladly add it to the project. Please test all changes locally before submitting and don't assume others will do that for you.

## What quality standards are in effect? ##

For architectural or project organization changes, please discuss these with the other contributors and attempt for form a consensus of some sort first.  Otherwise, you could end up doing a lot of work that will just get backed out.

## I want to help, but am too unskilled and/or stupid. ##

One option here would be to help out with testing, documentation, managing various aspects of this site, assisting other users in the forum or on IRC, helping the community on a public server, or anything else you can think of. This kind of community help has been essential to this project (especially the testing).

## What are Milestone builds? ##

Milestone builds are released to help those of you who may lack some of the technical skills needed to setup your own server via compiling and applying updates. If you find compilers and databases intimidating, these are for you.

Milestone 1: This was an Ep5 release. It is still available on the old project site for those wanting to run an Ep5 server.

Milestone 2: This was a post-EpU merge release. It also is only available on the old site.

Milestone 3: This was an internal re-baselining milestone for correcting some of the bad development work that occurred during and in the 6 weeks after the EpU merge. No precompiled server binary exists for this release.

Milestone 4: This milestone has been published after the first l1j-en update completed testing and went live on the LineageDC public server.

Milestone 5: This is a maintenance update with a ton of minor bugfixes.

Milestone 6: Another maintenance update.

Milestone 7: The main 3.0 update.

Milestone 8: The stabilization update for all new 3.0 content.

Milestone 9: In progress.  Currently targeting feature coverage, codebase refactoring, and even more fixes.

## What's the current status? ##

Work on Lineage 1 emulation in general slowed down quite a bit post-2012.  However, there seems to be renewed interest among key participants, so things are definitely active again.

# Using l1j-en #

## Castle sieges don't work! ##

They work, you just need to set them up properly. If this is on your own server, to do so, shutdown the server then manually set the castle ownership to a test/gm prince by changing that bp's hasCastle value in clan\_data. Log in as that char to verify that he does in fact own the castle (you should see a crown over his head and be able to use BP return scrolls). Then, shut the server down again and set the castle's war\_time to the near future in local time. Other princes should now be able to declare as long as they follow the normal game rules for being able to do so. Do this for all of your castles until player bps own all of them.