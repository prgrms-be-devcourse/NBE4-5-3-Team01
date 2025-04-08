import java.time.LocalDate

data class MembershipDto(
    val grade: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val autoRenew: Boolean
)
