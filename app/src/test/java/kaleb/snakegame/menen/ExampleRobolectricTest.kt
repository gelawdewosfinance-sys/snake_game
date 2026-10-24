package kaleb.snakegame.menen

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kaleb.snakegame.menen.model.Direction
import kaleb.snakegame.menen.model.SnakeSkin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Snake Game", appName)
  }

  @Test
  fun `test initial direction opposite check`() {
    assertTrue(Direction.DOWN.isOpposite(Direction.UP))
    assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT))
  }

  @Test
  fun `test default skin is emerald`() {
    assertEquals("emerald", SnakeSkin.EMERALD_VIPER.skinId)
  }
}
