package com.divyanshu.draw.widget.contract.design.command

import java.util.*

interface ICommandManager {
    val recordF: Stack<ICommand>
    val recordB: Stack<ICommand>
}