# Coppin

![Fanny Jackson Coppin](https://upload.wikimedia.org/wikipedia/commons/c/cf/Fanny_jackson_coppin_headshot.jpg)  
[Fanny Jackson Coppin](https://www.coppin.edu/about/coppin-pride/fanny-jackson-coppin)

Coppin is an assignment creator and grader meant to be used with [Doctor](https://github.com/EmpowrOrg/Doctor)
and [CodeEditorXblock](https://github.com/EmpowrOrg/CodeEditorXblock). By pairing these three tools, any organization that utilizes
Open Edx will have the ability to teach and grade coding assignments.

Empowr's goal is to create an equitable future for all, and therefore we believe in open-source technologies.

___

## Technology

We use Ktor as it's a fast and easy to learn framework for server creation. Exposed is used to make working with the
database easier and offer a layer of protection. 
___

## How you can support

The number one way to support this project is by donating to [Empowr](https://empowrco.org). If you are technically
inclined and would like to submit code, then feel free to contribute to any area on the following list.

- Security. While this serves only serves as a base, we would like to ensure it is as secure as possible.
- More Languages. We would love to support more languages on the server side.
- Bugs & Features. We are open to adding new features. So if you have a cool idea, create it!
- Scalability. Add Redis Cache. SQL overview. Anything that can improve the performance and effeciency.

___

## How it works by the module

### Command

The command module is responsible for running commands locally on the server hardware

#### Commander

This file is used for executing commands locally. You simply pass in the command
to your `Commander`'s `execute()` method

### buildSrc
This is a module used solely for dependency management.

### Assignment
This module contains the API's used by [Doctor](https://github.com/EmpowrOrg/Doctor) for grading and creating assignments.

___

    Coppin is a programming  assignment creator and grader.
    Copyright (C) 2022 Empowr

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
