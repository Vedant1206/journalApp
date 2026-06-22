# Journal App — Study Notes

> Running notes from my Spring Boot learning. Each entry = a doubt I had + the answer, for later revision.

---

## 1. Generating getters & setters in IntelliJ

- Click **inside the class body** (between `{` and `}`), then press **`⌘N`** → **Generate** → **Getter and Setter**.
- **Gotcha:** if your cursor is *outside* the class (e.g. after the closing `}`), the Generate menu only shows class-level options (Spring Component, Copyright) — NOT getter/setter. Member-level options only appear when the cursor is inside the class.

---

## 2. `Map` and `HashMap`

- A **`Map`** stores data as **key → value pairs** (like a dictionary: look up a key, get its value). Fast lookup by key.
- `Map<Long, JournalEntry>` = "keys are `Long`, values are `JournalEntry`".
- **`Map` vs `HashMap`:**
  - `Map` = the **interface** (the contract: what a map can do). Can't instantiate it directly.
  - `HashMap` = an **implementation** (the actual class doing the work).
  - Standard style: `Map<...> x = new HashMap<>();` — declare by interface, build with implementation.
- Common methods: `.put(key, value)`, `.get(key)`, `.values()`, `.remove(key)`.
- **Gotcha:** Java is case-sensitive — it's `HashMap`, not `Hashmap`.

---

## 3. A method with a return type must actually `return`

- `public List<X> getAll() { }` → compile error "missing return statement". The body must return the declared type, e.g. `return new ArrayList<>(map.values());`.

---

## 4. `@PathVariable` needs a matching `{...}` in the mapping path

- `@PathVariable Long id` pulls a value **out of the URL path**, so there must be a `{id}` placeholder in the mapping.
- ❌ Broken: `@PutMapping` (no path) + `@PathVariable Long id` → "Missing URI template variable 'id'".
- ✅ Fixed: `@PutMapping("/id/{id}")` + `@PathVariable Long id` → URL `PUT /Journal/id/5`.

---

## 5. 404 from URL not matching the mapping

- The full URL = class-level `@RequestMapping` + method-level mapping. e.g. `@RequestMapping("/Journal")` + `@DeleteMapping("/delete/{id}")` = `/Journal/delete/{id}`.
- `{id}` is a **placeholder** — in the real URL you type the *value*, not the word "id". To delete entry 1: `DELETE /Journal/delete/1` (NOT `/Journal/delete/id/1`).
- A URL that matches no mapping → **404 Not Found**.

---

## 6. Installing MongoDB on a Mac

- **Two different things:**
  - **Local MongoDB** (`mongodb-community`) — runs on my Mac at `localhost:27017`. Best for learning. ✅
  - **MongoDB Atlas** (`mongodb-atlas-cli`) — the *cloud* service; needs a cloud account/org/project. Not the database itself.
- **Install + run local:**
  ```bash
  brew tap mongodb/brew
  brew install mongodb-community
  brew services start mongodb-community
  brew services list        # check it shows "started"
  ```
- **Compass** (GUI to view data): `brew install --cask mongodb-compass`, then connect to `mongodb://localhost:27017`. Compass is just a viewer — the database must be running for it to connect.

---

## 7. How do endpoints work if I never call the controller? (`@SpringBootApplication`)

- I never write `new JournalEntryController()`, yet the endpoints work. **Spring does it via component scanning + dependency injection.**
- `@SpringBootApplication` bundles `@ComponentScan`, which scans the **main class's package and all sub-packages** for annotated classes (`@RestController`, `@Service`, etc.) and **creates instances (beans)** of them automatically.
- For a `@RestController`, Spring reads the `@GetMapping`/`@PostMapping` annotations and builds a **routing table**, then the embedded Tomcat server calls the right method when a matching HTTP request arrives.
- **This is Inversion of Control (IoC):** *I* declare classes with annotations; *Spring* creates them, wires them, and calls them.
- **Gotcha:** keep the main app class in the **top-level package** (above everything). A controller outside the scanned tree is never found → silent 404.

---

## 8. Spring stereotype annotations (the "tiers")

All of these make a class a **bean** (Spring creates + manages it). `@Component` is the base; the others = `@Component` + a tier label.

| Annotation | Tier / role |
|---|---|
| `@RestController` / `@Controller` | Web entry point (where a request starts). `@RestController` returns JSON; `@Controller` returns web pages. |
| `@Service` | Business-logic tier |
| `@Repository` | Data-access tier (talks to DB; also translates DB exceptions) |
| `@Component` | Generic, no special tier |

Mental model (request flow): **Controller → Service → Repository → Database.**

---

## 9. Entity vs Bean (important!)

| | **Bean** (Controller/Service/Repository) | **Entity / data object** (`JournalEntry`) |
|---|---|---|
| Represents | A worker / machinery that *does* things | A piece of data |
| How many | One shared instance | Many — one per record |
| Created by | Spring | Me (`new`), or from JSON, or from DB |
| Lifespan | Whole app lifetime | Transient — comes and goes |

- Analogy: **beans are the pipes; entities are the water flowing through them.**
- Don't make an entity a bean — there'd be only one shared instance for all data, which is nonsense.
- "Entity" = a data object that maps to a DB row/document.

---

## 10. `pom.xml` showing red

- Often **not a real error** — IntelliJ just hasn't re-imported Maven after the file changed.
- Fix: **Maven tool window → Reload (🔄)**, or right-click `pom.xml` → **Maven → Reload Project**.
- Empty tags like `<name/>`, `<description/>` are harmless **warnings** (yellow), not errors.

---

## 11. `MongoRepository` setup

- It's a **generic** interface — needs two type parameters: `MongoRepository<EntityType, IdType>`.
  ```java
  public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> { }
  ```
- The **second type must match the entity's `@Id` field type** (for Mongo, usually `String`).
- The interface body is **empty** because Spring Data generates all CRUD methods (`save`, `findById`, `findAll`, `deleteById`, …) for me.

---

## 12. Why is something RED in IntelliJ?

- **Red = unresolved symbol.** Usually a **missing `import`**. Java doesn't know a class unless it's imported (or fully-qualified).
- Fix: click the red name → **`⌥ + Enter`** → **"Import class"**.
- **Clue:** if the quick-fix only offers **"Create interface/class"** and NOT **"Import class"**, then the library isn't on the classpath at all → **Maven hasn't loaded the dependency.** Reload Maven (and if needed, **File → Invalidate Caches → Invalidate and Restart**).

---

## 13. Viewing library source (e.g. `MongoRepository`) in IntelliJ

- Put cursor on the symbol → **`⌘B`** (Go to Declaration), or `⌘+Click`.
- **`⌘F12`** = Structure popup → lists all methods.
- Banner **"Download Sources"** fetches the real commented `.java` (instead of decompiled bytecode).
- Only works once the symbol resolves (after Maven loaded).

---

## 14. `@Autowired` showing red/yellow

- Cause: the **enclosing class isn't a Spring bean.** `@Autowired` can only inject into a class Spring manages.
- Fix: add a stereotype annotation (e.g. **`@Service`**) on the class. Importing it isn't enough — it must be **applied** on the class.
- **Remember:** `@Service` = "I am a bean." `@Autowired` = "give me a bean." Both sides must be beans.

---

## 15. Checking & changing Java / Spring Boot versions

- **Spring Boot version** → `<parent><version>` in `pom.xml`.
- **Java level** → `<java.version>` property in `pom.xml`.
- **Installed JDKs** → `/usr/libexec/java_home -V`.
- **Downgrading (Spring Boot 4 → 2.7, Java → 8):**
  - Parent version → `2.7.18`; `<java.version>` → `8`.
  - **Starter names changed!** Spring Boot 4 used `spring-boot-starter-webmvc` / `spring-boot-starter-webmvc-test`; Spring Boot 2.7 uses **`spring-boot-starter-web`** / **`spring-boot-starter-test`**.
  - Must also point **IntelliJ** at JDK 8: File → Project Structure → SDK = `corretto-1.8`, Language level = 8 → then reload Maven.
  - Spring Boot 2.7 doesn't run reliably on very new JDKs (26) — need an actual JDK 8/11/17 installed.

---

## 16. Ways to create a bean

1. **Stereotype annotation + component scan** — `@Component`/`@Service`/`@Repository`/`@Controller`/`@RestController` on a class in the scanned package tree. (For my own classes.)
2. **`@Bean` method** inside a `@Configuration` class — for classes I can't annotate (e.g. library classes):
   ```java
   @Configuration
   class AppConfig { @Bean ObjectMapper objectMapper() { return new ObjectMapper(); } }
   ```
3. **Auto-configuration** — Spring Boot creates beans automatically based on classpath (e.g. Mongo beans + my repository's generated implementation). This is why I can `@Autowired` the repository without annotating it.

- `@Component`/`@Service` = "find this class and build it." `@Bean` = "call this method, keep what it returns." Both → a managed bean.

---

## 17. `@Document` (Spring Data MongoDB)

- Marks an **entity class as a MongoDB document** — "objects of this class are stored as documents in a collection."
- From `org.springframework.data.mongodb.core.mapping.Document`.
  ```java
  @Document(collection = "journal_entries")
  public class JournalEntry {
      @Id private String id;     // maps to Mongo's "_id"
      private String title;
      private String content;
  }
  ```
- `collection = "..."` names the Mongo collection (defaults to the lowercased class name).
- MongoDB terms: **collection** ≈ SQL table; **document** ≈ row.
- **`@Document` does NOT make it a bean** — it's a *mapping* annotation (tells Spring how to store the object). Different job from `@Service`.

---

## 18. Using `mongosh` (the Mongo shell) to inspect data

`mongosh` is the command-line client for poking at the database directly (alternative to the Compass GUI).

### Connect
```bash
mongosh
```
- With no args it connects to the local server at `mongodb://127.0.0.1:27017` (default).
- The startup warning *"Access control is not enabled"* just means no username/password is required — normal & fine for local dev.
- The prompt starts as `test>` (the default db). It changes to show whichever db I'm in, e.g. `journaldb>`.

### Hierarchy reminder
**server → databases → collections → documents** (≈ server → schemas → tables → rows in SQL).

### Core commands
| Command | What it does |
|---|---|
| `show dbs` | list all databases (with sizes) |
| `use journaldb` | switch into the `journaldb` database (creates it lazily on first write) |
| `db` | show which db I'm currently in |
| `show collections` | list collections in the current db |
| `db.journal_entries.find()` | show **all** documents in the `journal_entries` collection |
| `db.journal_entries.find().pretty()` | same, nicely formatted |
| `db.journal_entries.findOne({ _id: '2' })` | find one document by a field |
| `db.journal_entries.countDocuments()` | count documents |
| `db.journal_entries.deleteOne({ _id: '2' })` | delete one matching document |
| `db.journal_entries.drop()` | delete the whole collection |
| `exit` (or Ctrl-D) | quit the shell |

### Reading a returned document
```js
{
  _id: '2',
  title: 'Morning',
  content: 'Morning was ehhhhh',
  _class: 'com.mySpringBootApplication.journalApp.entity.JournalEntry'
}
```
- **`_id`** — the document's primary key (maps to my entity's `@Id` field).
- **`title` / `content`** — my entity's fields, stored as-is.
- **`_class`** — added automatically by **Spring Data**. It records the Java class the document came from, so Spring knows what type to rebuild when reading it back. (Mongo itself doesn't need it; it's Spring's bookkeeping.)

### Filtering
`find()` takes a query object: `db.journal_entries.find({ title: 'Morning' })` returns only docs where `title` is `"Morning"`. Empty `find()` = everything.

---

## 19. Type vs value (`LocalDateTime` vs `LocalDateTime.now()`)

- `LocalDateTime` is a **type** (class name). You can't pass a type where a value is expected.
- ❌ `myEntry.setDate(LocalDateTime);` → "expression expected / LocalDateTime is a type".
- ✅ `myEntry.setDate(LocalDateTime.now());` → `.now()` is a method that **returns** a `LocalDateTime` value.
- Same idea: `setTitle("Morning")` (value), never `setTitle(String)`.

---

## 20. Repository method return types (`save` / `findById` / `deleteById`)

| Method | Returns | Notes |
|---|---|---|
| `save(entity)` | the saved entity | gives back the stored object (with generated id, etc.) |
| `findById(id)` | **`Optional<Entity>`** | might not exist — unwrap with `.orElse(null)`, or return the `Optional` and let the caller handle empty |
| `findAll()` | `List<Entity>` | all documents |
| `deleteById(id)` | **`void`** | nothing to return — don't `return` its result; make the method `void` (or return your own `boolean`) |

- **`Optional<T>`** = a wrapper meaning "maybe a value, maybe nothing." Forces handling the not-found case instead of risking `NullPointerException`.

---

## 21. Service vs Entity variable-type mix-up

- `JournalEntryService` = the **bean** (the worker). `JournalEntry` = the **data object** it returns.
- ❌ `JournalEntryService temp = journalEntryService.findUsingId(id);` → the method returns a `JournalEntry`, not a service.
- ✅ `JournalEntry temp = journalEntryService.findUsingId(id);`
- Same bean-vs-data-object idea from note #9 — watch the variable's declared type.

---

## 22. Updates must call `save()` — and other update gotchas

When I wrote an update endpoint, nothing persisted. Three bugs:
1. **Never saved.** Changing a fetched Java object does NOT update the DB. Must call `repository.save(obj)` (via the service) after modifying it.
2. **Returned `null`** instead of the updated entry.
3. **Backwards ternary.** `newTitle != null && newTitle.equals("")` only overwrites when the new title is *empty*. Correct condition: `newTitle != null && !newTitle.isEmpty()` (present AND not empty → use it).

Correct pattern:
```java
JournalEntry old = service.findUsingId(id);
if (old != null) {
    old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
    old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : old.getContent());
    service.saveEntry(old);   // persist!
}
return old;
```

---

## 23. BIG lesson: `Object` id → mixed-type ids → deletes silently fail

- Entity had `@Id private Object id;` and repo `MongoRepository<JournalEntry, Object>`.
- Because the type was `Object`, the stored `_id` took whatever JSON type I sent: `"6"` (string), `6` (number), or an auto ObjectId. The DB ended up with a **mix of id types**.
- A URL **`@PathVariable` is always a String**. So `DELETE /Journal/delete/6` calls `deleteById("6")` (string). But the doc was stored as the **number** `6`. In MongoDB **`"6"` (string) ≠ `6` (number)** → no match → **delete silently does nothing** (no error!).
- **Fix: standardize the id type to `String` everywhere:**
  - Entity: `@Id private String id;` (+ `getId()`/`setId(String)`)
  - Repository: `MongoRepository<JournalEntry, String>`
  - Service: methods take `String id`
  - Controller: `@PathVariable String id`
  - Always send ids as JSON strings: `{"id": "6"}`.
- Clean up bad existing data in mongosh: `db.journal_entries.deleteMany({ _id: { $type: "number" } })` or `db.journal_entries.deleteMany({})` to wipe all.
- **Takeaway:** keep the id type consistent across entity ↔ repository ↔ service ↔ controller ↔ the JSON you send. Avoid `Object` ids.

---

## 24. Restart the app after code changes

- Editing Java code does **not** update the already-running Spring Boot server — it loads code at startup.
- After changing endpoints/logic, **stop and re-run the app** or the old behavior persists (or you get 404s on new endpoints).
- (Spring Boot DevTools can auto-restart on change, but by default you restart manually.)

---
