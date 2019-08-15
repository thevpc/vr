# vr-box
Vain Ruling Web Framework (VRWF) code generation tool

**vr-box** is a useful companion tool for the VR Web framework. It helps creation and managing of applications based on VRWF.

## Installation
**vr-box** is installable using [nuts](https://github.com/thevpc/nuts) tool. 

Please check that nuts version is 0.5.5 or newer is instaleld than type

```
nuts install -y vr-box
```
You should see something like

```
net.vpc.app.nuts.toolbox:vr-box#1.13.15.0 installed successfully. Set as default.
```

## Requirements
nuts version 0.5.5 or late
Java Runtime Environment (JRE) or Java Development Kit (JDK) version 8 or later

## Creating a new project using vr-box
To create a new project you need to type
```
cd /your/path
nuts vr-box -y n <project-id> <project-options>
```
For example, to create a project named com.company:my-project#1.0 with all edu plugins enable, just type

```
cd /your/path
nuts vr-box -y new com.company:my-project#1.0 --edu
```

for more help type
nuts vr-box --help

