import 'package:flutter/foundation.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';

class AdService {
  static const String appId = 'ca-app-pub-7621875430117912~5112925480';
  static const String appOpenAdUnitId = 'ca-app-pub-7621875430117912/8018591749';
  static const String bannerAdUnitId = 'ca-app-pub-7621875430117912/3608272128';
  static const String interstitialAdUnitId = 'ca-app-pub-7621875430117912/1930947116';
  static const String rewardedAdUnitId = 'ca-app-pub-7621875430117912/8198428838';

  static BannerAd? _bannerAd;
  static bool _isBannerLoaded = false;
  static InterstitialAd? _interstitialAd;
  static RewardedAd? _rewardedAd;

  static Future<void> initialize() async {
    try {
      await MobileAds.instance.initialize();
      loadBanner();
      loadInterstitial();
      loadRewarded();
    } catch (e) {
      debugPrint('AdMob initialization error: $e');
    }
  }

  static void loadBanner() {
    _bannerAd = BannerAd(
      adUnitId: bannerAdUnitId,
      size: AdSize.banner,
      request: const AdRequest(),
      listener: BannerAdListener(
        onAdLoaded: (ad) {
          _isBannerLoaded = true;
        },
        onAdFailedToLoad: (ad, error) {
          ad.dispose();
          _isBannerLoaded = false;
        },
      ),
    )..load();
  }

  static BannerAd? get bannerAd => _isBannerLoaded ? _bannerAd : null;

  static void loadInterstitial() {
    InterstitialAd.load(
      adUnitId: interstitialAdUnitId,
      request: const AdRequest(),
      adLoadCallback: InterstitialAdLoadCallback(
        onAdLoaded: (ad) => _interstitialAd = ad,
        onAdFailedToLoad: (error) => _interstitialAd = null,
      ),
    );
  }

  static void showInterstitial({required VoidCallback onDismissed}) {
    if (_interstitialAd != null) {
      _interstitialAd!.fullScreenContentCallback = FullScreenContentCallback(
        onAdDismissedFullScreenContent: (ad) {
          ad.dispose();
          loadInterstitial();
          onDismissed();
        },
        onAdFailedToShowFullScreenContent: (ad, error) {
          ad.dispose();
          loadInterstitial();
          onDismissed();
        },
      );
      _interstitialAd!.show();
    } else {
      loadInterstitial();
      onDismissed();
    }
  }

  static void loadRewarded() {
    RewardedAd.load(
      adUnitId: rewardedAdUnitId,
      request: const AdRequest(),
      rewardedAdLoadCallback: RewardedAdLoadCallback(
        onAdLoaded: (ad) => _rewardedAd = ad,
        onAdFailedToLoad: (error) => _rewardedAd = null,
      ),
    );
  }

  static void showRewarded({
    required Function(RewardItem reward) onUserEarnedReward,
    required VoidCallback onAdDismissed,
  }) {
    if (_rewardedAd != null) {
      _rewardedAd!.fullScreenContentCallback = FullScreenContentCallback(
        onAdDismissedFullScreenContent: (ad) {
          ad.dispose();
          loadRewarded();
          onAdDismissed();
        },
        onAdFailedToShowFullScreenContent: (ad, error) {
          ad.dispose();
          loadRewarded();
          onAdDismissed();
        },
      );
      _rewardedAd!.show(onUserEarnedReward: (ad, reward) {
        onUserEarnedReward(reward);
      });
    } else {
      loadRewarded();
      onAdDismissed();
    }
  }
}
