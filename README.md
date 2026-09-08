# CleanroomModTemplate
Mod development template for Cleanroom, uses a custom [Unimined fork](https://github.com/kappa-maintainer/Unimined) ([original](https://github.com/unimined/Unimined))

### WARNING: Custom Unimined Fork
May have issues, report here or [here](https://github.com/kappa-maintainer/Unimined) when you encountered impossible field names or impossible Scala compiler errors. 

## Java language server (OMP, Linux)

The project uses [Eclipse JDT LS](https://github.com/eclipse-jdtls/eclipse.jdt.ls)
for Java 25 and Gradle/Buildship support. OMP loads `.omp/lsp.json`; no mod
runtime dependency is added.

Install the pinned [1.61.0 release](https://download.eclipse.org/jdtls/milestones/1.61.0/)
once outside the checkout (Python 3.9+ is required by its launcher):

```sh
archive="$(mktemp)"
curl --fail --location --output "$archive" \
  https://download.eclipse.org/jdtls/milestones/1.61.0/jdt-language-server-1.61.0-202609031315.tar.gz
echo "338e7e73d61836651ba2453919a0d34fa763eb4e7c03342092309bffb8934c64  $archive" | sha256sum --check - &&
  mkdir -p "$HOME/.local/share/jdtls/1.61.0" &&
  tar -xzf "$archive" -C "$HOME/.local/share/jdtls/1.61.0"
rm "$archive"
```

Set `JAVA_HOME` to JDK 25. If unset, `.omp/jdtls.py` checks Java on `PATH`,
then JDKs already provisioned in `${GRADLE_USER_HOME:-$HOME/.gradle}/jdks`.
An explicitly set `JAVA_HOME` must be Java 25. Set `JDTLS_HOME` to use a
different extracted JDT LS distribution; the launcher does not download tools.

The server uses the checked-in Gradle wrapper. Initial import may download
Gradle and dependencies. `gradle/scripts/extra.gradle` conditionally exposes
Unimined's source-set compile classpath to Buildship and runs Blossom's
`generateJavaTemplates` during synchronization, so Minecraft/Forge types and
the generated `Reference` class resolve.

Workspace/configuration metadata stays under
`${XDG_CACHE_HOME:-$HOME/.cache}/schmaloogium/jdtls/<checkout-path-hash>/`;
Eclipse compiler output in `/bin/` is ignored. `reference-src/` is excluded
from project import. Separate checkout paths receive separate workspaces.

In OMP, call LSP `reload` with `file: "*"` to discover the configuration, then
request `symbols` or `diagnostics` on
`src/main/java/com/example/modid/ExampleMod.java`. After changing the launcher
or Java environment, restart the OMP session/server; `reload` alone normally
refreshes settings rather than replacing a live Java process.

### Running Client or Server
If you are using IntelliJ, **DO NOT** use the `Minecraft Client` configure with a blue icon. Just use the `2. Run Client` Gradle task.

### Adding Mod Dependencies
You can find dependencies block in `gradle/scripts/dependencies.gradle`.

No more `rfg.deobf()` or `fg.deobf`. You **MUST** add mods by using `modImplementation` or `modRuntimeOnly`, or the game will crash when running.

Use `modLibrary` for libs/mods that you don't want to remap.

### Non-Mod Dependencies
Two new configuration types `contain` and `shadow` are available, check more details in `dependencies.gradle`.

### gradle.properties
Edit gradle.properties and set your modid, mod version, mod name, package, etc.

If you are writing a coremod, remember to set related settings to true.

### Reference Class
There will be a `Reference` class under your top package.

This is used to store mod version so you can fill it to `@Mod` annotation.

You should change its location to fit your new package name.

You can find its template under `src/main/java-templates`.

### Mixin
1. Rename json config file to use your modid. 
2. Add **all** your mixin classes there.
3. Use `IMixinConfigPlugin` to control if certain mixin should be enabled.
4. mixin classes will be passed to the plugin only when target class is loading, so you don't need to call `Loader.isModLoaded()`
5. Don't worry about refmap, Unimined will handle it automatically. You can still `disableRefmap()` manually though

### Access Transformer
You **MUST** write AT file in MCP name. It will be remapped back to SRG name in artifact jar.

Rename AT file name to your modid before using it. There's an example entry in AT file, remove it if you want to use AT.

### Vanilla Source Code with Comments
Run `genSources` task in gradle. If it didn't work, run again until a file with `-sources.jar` suffix appeared.

If you want to `find usage` from vanilla like RFG, just change the scope in IntelliJ settings.

### GitHub Action
This template comes with three workflows.

`build.yml` will build and upload artifact for every commit. Useful when you want to provide test builds for debugging.

`release.yml` will make a GitHub release if you pushed a git tag.

`release-to-cf-mr.yml` can publish your mod to CurseForge and/or Modrinth.

You need to fill in your project IDs and configure your tokens in GitHub repository first.

By default, you will need to manually trigger the workflow in web page, but you can also enable tag triggering by merging the third yml into `release.yml`.