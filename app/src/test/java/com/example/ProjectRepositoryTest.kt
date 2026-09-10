package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PuterTemplates
import com.example.data.model.SupportedModels
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProjectRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: ProjectRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val fakeDao = FakeProjectDao()
        repository = ProjectRepository(context, projectDaoProvider = { fakeDao })
    }

    @Test
    fun `test model preferences management`() {
        // Defaults
        assertEquals(SupportedModels.DEFAULT_MODEL_ID, repository.getSelectedModelId())
        assertEquals(SupportedModels.DEFAULT_NVIDIA_API_KEY, repository.getNvidiaApiKey())
        assertEquals(SupportedModels.DEFAULT_NVIDIA_BASE_URL, repository.getNvidiaBaseUrl())

        // Switch to Gemini
        repository.setSelectedModelId("gemini-3.1-pro-preview")
        assertEquals("gemini-3.1-pro-preview", repository.getSelectedModelId())

        // Update keys and endpoints
        repository.saveNvidiaApiKey("nvapi-custom-test-key-1234")
        assertEquals("nvapi-custom-test-key-1234", repository.getNvidiaApiKey())

        repository.saveNvidiaBaseUrl("https://custom.nim.endpoint/v1")
        assertEquals("https://custom.nim.endpoint/v1", repository.getNvidiaBaseUrl())

        repository.saveGeminiApiKey("gemini-custom-test-key-5678")
        assertEquals("gemini-custom-test-key-5678", repository.getGeminiApiKey())
    }

    @Test
    fun `test project creation and version revert`() = runBlocking {
        val projId = repository.initializeDefaultProjectIfNeeded()
        assertNotNull(projId)

        val project = repository.getProjectDirect(projId)
        assertNotNull(project)
        assertEquals(1, project?.currentVersion)
        assertTrue(project?.title?.isNotBlank() == true)
        assertTrue(project?.htmlContent?.isNotBlank() == true)

        // Modify files
        repository.updateProjectFiles(
            projectId = projId,
            html = "<h1>Updated App</h1>",
            css = "body { background: #000; }",
            js = "console.log('updated');",
            manifest = "{}"
        )

        val updated = repository.getProjectDirect(projId)
        assertEquals("<h1>Updated App</h1>", updated?.htmlContent)
        assertEquals("body { background: #000; }", updated?.cssContent)

        // Revert to initial version
        repository.revertToVersion(projId, 1)
        val reverted = repository.getProjectDirect(projId)
        assertEquals(1, reverted?.currentVersion)
        assertTrue(reverted?.htmlContent?.contains("<!DOCTYPE html>") == true)
    }

    @Test
    fun `test loading different task templates`() = runBlocking {
        // Test all curated Puter templates representing different task types
        val templates = PuterTemplates.templates
        assertEquals(4, templates.size)

        for (template in templates) {
            val projId = repository.loadTemplate(template.id)
            assertNotNull(projId)
            val project = repository.getProjectDirect(projId)
            assertNotNull(project)
            assertEquals(template.title, project?.title)
            assertTrue(project?.htmlContent?.contains("<!DOCTYPE html>") == true)
            assertTrue(project?.cssContent?.isNotBlank() == true)
            assertTrue(project?.jsContent?.isNotBlank() == true)
            assertTrue(project?.thinkingLog?.isNotBlank() == true)
        }
    }

    @Test
    fun `test parsing task output with standard delimiter markers`() {
        val rawAiOutput = """
=== THINKING ===
Task: Build a Retro Cyberpunk Synth Drum Pad.
Architecture:
- Web Audio API AudioContext with synthesized 808 kick, snare, hi-hat, and synth lead.
- Puter KV used to save custom drum beat sequences.
- Canvas-based neon oscilloscope visualizer.

=== FILE: index.html ===
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Cyberpunk Synth</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
  <div class="drum-grid"></div>
  <script src="app.js"></script>
</body>
</html>

=== FILE: styles.css ===
body {
  background: #0a0a12;
  color: #00ffcc;
  font-family: monospace;
}
.drum-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
}

=== FILE: app.js ===
const ctx = new (window.AudioContext || window.webkitAudioContext)();
function playKick() {
  const osc = ctx.createOscillator();
  osc.frequency.setValueAtTime(150, ctx.currentTime);
  osc.frequency.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
  osc.connect(ctx.destination);
  osc.start();
  osc.stop(ctx.currentTime + 0.5);
}

=== FILE: manifest.json ===
{
  "name": "Cyberpunk Synth",
  "short_name": "Synth"
}
        """.trimIndent()

        val parsed = repository.parseGeneratedContent(rawAiOutput)
        assertTrue(parsed.thinking.contains("Retro Cyberpunk Synth"))
        assertTrue(parsed.html.contains("<title>Cyberpunk Synth</title>"))
        assertTrue(parsed.css.contains(".drum-grid"))
        assertTrue(parsed.js.contains("AudioContext"))
        assertTrue(parsed.manifest.contains("Cyberpunk Synth"))
    }

    @Test
    fun `test parsing task output with markdown code blocks fallback`() {
        val rawMarkdownOutput = """
Here is your application for a Puter Cloud KV Note Taker:

```html
<!DOCTYPE html>
<html>
<body>
  <textarea id="note"></textarea>
  <button id="saveBtn">Save to Puter</button>
</body>
</html>
```

```css
body {
  background: #111;
  color: #fff;
}
textarea {
  width: 100%;
  height: 200px;
}
```

```javascript
document.getElementById('saveBtn').addEventListener('click', async () => {
  const text = document.getElementById('note').value;
  if (window.puter && window.puter.kv) {
    await window.puter.kv.set('my_note', text);
  }
});
```
        """.trimIndent()

        val parsed = repository.parseGeneratedContent(rawMarkdownOutput)
        assertTrue(parsed.html.contains("Save to Puter"))
        assertTrue(parsed.css.contains("textarea"))
        assertTrue(parsed.js.contains("window.puter.kv.set"))
    }
}
