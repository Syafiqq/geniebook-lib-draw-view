package com.divyanshu.draw.widget.impl.command

import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.contract.design.command.ICommand

class DrawCommand(private val container: ArrayList<IMode>, private var draw: IMode) : ICommand {
    override fun up() {
        container.add(draw)
    }

    override fun down() {
        container.remove(draw)
    }
}