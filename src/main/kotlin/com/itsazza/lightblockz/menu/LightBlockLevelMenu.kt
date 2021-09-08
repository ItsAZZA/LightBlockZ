package com.itsazza.lightblockz.menu

import com.itsazza.lightblockz.LightBlockZ.Companion.instance
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import de.tr7zw.changeme.nbtapi.NBTContainer
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.data.type.Light
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class LightBlockLevelMenu {
    private var block: Block? = null
    private var stack: ItemStack? = null
    private lateinit var mode: EditMode

    fun open(player: Player, mode: EditMode, block: Block? = null, stack: ItemStack? = null) {
        this.block = block
        this.stack = stack
        this.mode = mode
        create().show(player)
    }

    private fun create(): InventoryGui {
        val gui = InventoryGui(
            instance,
            instance.getLangString("gui-light-level-menu-name"),
            arrayOf(
                "         ",
                " 0000000 ",
                " 0000000 ",
                " 0000000 ",
                "    c    ",
            )
        )

        gui.addElement(
            'c',
            ItemStack(Material.BARRIER),
            {
                it.gui.destroy()
                return@addElement true
            },
            instance.getLangString("gui-close-button")
        )

        val group = GuiElementGroup('0')
        for (i in 0..15) {
            group.addElement(createLightButton(i))
        }

        gui.addElement(group)
        gui.setFiller(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
        gui.setCloseAction { false }
        return gui
    }

    private fun createLightButton(level: Int): StaticGuiElement {
        val item = NBTItem(ItemStack(Material.LIGHT)).also {
            val tag = NBTContainer("{BlockStateTag: {level: $level}}")
            it.mergeCompound(tag)
        }.item

        val name = instance.getLangString("gui-light-level-button-name").format(level)
        val description = instance.getLangString("gui-light-level-button-description").format(level)

        return StaticGuiElement(
            '!',
            item,
            {
                val player = it.event.whoClicked as Player
                when (mode) {
                    EditMode.BLOCK -> {
                        val data = block!!.blockData as Light
                        data.level = level
                        block!!.blockData = data
                        player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f + 0.1f * level)
                        return@StaticGuiElement true
                    }
                    EditMode.STACK -> {
                        if (level == 15) {
                            player.inventory.setItemInMainHand(ItemStack(Material.LIGHT, stack!!.amount))
                        } else {
                            val nbtItem = NBTItem(stack)
                            nbtItem.mergeCompound(NBTContainer("{BlockStateTag: {level: $level}}"))
                            player.inventory.setItemInMainHand(nbtItem.item)
                        }
                        return@StaticGuiElement true
                    }
                }
            },
            name,
            *description.split('\n').toTypedArray()
        )
    }

    enum class EditMode {
        BLOCK, STACK
    }
}