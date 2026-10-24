package kaleb.snakegame.menen.ads

/**
 * Configuration and unit identifiers for AdMob services.
 */
object AdMobConfig {
  const val APP_ID = "ca-app-pub-7621875430117912~5112925480"
  const val APP_OPEN_AD_ID = "ca-app-pub-7621875430117912/8018591749"
  const val BANNER_AD_ID = "ca-app-pub-7621875430117912/3608272128"
  const val INTERSTITIAL_AD_ID = "ca-app-pub-7621875430117912/1930947116"
  const val REWARDED_AD_ID = "ca-app-pub-7621875430117912/8198428838"
}

/**
 * Active ad overlay types currently shown in the app.
 */
sealed class ActiveAd {
  object None : ActiveAd()
  data class AppOpen(val title: String = "Welcome to Snake Game") : ActiveAd()
  data class Interstitial(val reason: String = "Game Over") : ActiveAd()
  data class RewardedVideo(
    val purpose: RewardedAdPurpose,
    val videoCount: Int = 2,
    val secondsPerVideo: Int = 10
  ) : ActiveAd()
}

/**
 * Purpose of the rewarded video ad.
 */
sealed class RewardedAdPurpose {
  object ReviveThreeLives : RewardedAdPurpose()
  data class UnlockSkin(val skinId: String) : RewardedAdPurpose()
}
