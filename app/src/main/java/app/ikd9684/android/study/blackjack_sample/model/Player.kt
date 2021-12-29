package app.ikd9684.android.study.blackjack_sample.model

open class Player(
    val name: String,
) {
    override fun hashCode(): Int {
        return name.lowercase().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return name.lowercase() == (other as? Player)?.name?.lowercase()
    }

    override fun toString(): String {
        return "Player($name)"
    }
}
