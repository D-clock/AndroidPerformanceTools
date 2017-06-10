# AndroidPerformanceTools

本项目用于整理安卓性能监测方案，如果你有不错的方案推荐，欢迎给我提个Issue！

## Features

- 检测卡顿，并dump出卡顿发生时所有的堆栈信息，定位到卡顿源头。实现原理：[Android卡顿检测方案](http://blog.coderclock.com/2017/06/04/android/AndroidPerformanceTools-BlockLooper/)

## Usage

本项目目前没有打包上传至JCenter上，只能通过引入lib的方式进行使用，功能代码全部在library工程中，使用Demo在app工程中。

## Reference

感谢以下提供思路的（开源/闭源）项目！

- https://github.com/SalomonBrys/ANR-WatchDog