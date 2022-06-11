# cmdlib

BrigadierのKotlin用ラッパー

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Flepinoid.github.io%2Fmaven-repo%2Fnet%2Flepinoid%2Fcmdlib%2Fmaven-metadata.xml)

[uten2c](https://github.com/uTen2c)/[cmdlib](https://github.com/uTen2c/cmdlib)のフォーク版です．本人より許諾を得て改変・MITライセンスでの配布を行っております

> 1.18以降のバージョンではuten2c氏の[strobo](https://github.com/uTen2c/strobo)より移植しています。
> 
> 注意：**一部argumentはNMSが必須になります**

### Groovy DSL

```groovy
repositories {
    maven { url 'https://lepinoid.github.io/maven-repo/' }
}

dependencies {
    implementation 'net.lepinoid:cmdlib:VERSION'
}
```

### Kotlin DSL
```kotlin
repositories {
    maven("https://lepinoid.github.io/maven-repo/")
}

dependencies {
    implementation("net.lepinoid:cmdlib:VERSION")
}
```

## Example

```kotlin
val cmdLib = CmdLib(Plugin)

cmdLib.register("example") {
    requires("permission.name") // Bukkit permission name
    // requires(2) // or Minecraft op level
    // or custom
    // requires { sender ->
    //     sender is Player && sender.gameMode == GameMode.CREATIVE
    // }

    literal("echo") {
        executes {
            sender.sendMessage("Hello")
        }
    }

    literal("getDiamond") {
        //before 1.18
        integer("amount", 0, 64) {
            executes {
                val itemStack = ItemStack(Material.DIAMOND).apply {
                    amount = getInteger("amount")
                }
                player.inventory.addItem(itemStack)
            }
        }
        // 1.18~
        integer(0, 64) { getAmount ->
            executes {
                val itemStack = ItemStack(Material.DIAMOND).apply {
                    amount = getAmount()
                }
                player.inventory.addItem(itemStack)
            }
        }
    }

    literal("tp") {
        // before 1.18
        entity("target") {
            executes {
                val target = getEntity("target")
                player.teleport(target.location)
            }
        }
        // 1.18~
        entity { getTarget ->
            executes {
                player.teleport(getTarget().location)
            }
        }
    }
}
```

## 引数

see [CommandBuilder.kt](https://github.com/Lepinoid/cmdlib/blob/d2fcb22f483e80964a4c221f87b007cc1481a4a9/src/main/java/net/lepinoid/cmdlib/CommandBuilder.kt#L102)

## Tips

- `executes`内で`player`を使用するときコマンドの実行者がプレイヤーでない場合実行に失敗するので`sender`がプレイヤーであるかの検証をする必要はない
