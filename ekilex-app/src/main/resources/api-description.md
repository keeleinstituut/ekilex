**Overview**

The [Ekilex API](https://github.com/keeleinstituut/ekilex) is a RESTful web service that provides programmatic access to the [Ekilex](https://ekilex.ee) (Estonian Dictionary and Terminology Database System) maintained by the [Estonian Language Institute](https://eki.ee) (Eesti Keele Instituut – EKI).  
The API enables developers, researchers, and language professionals to query and retrieve lexical data such as words, definitions, meanings, and morphological information.

**Main Purpose**

Ekilex serves as the central dictionary and terminology database system for the Estonian language, consolidating Estonian vocabulary and terminology into a unified environment.  
It supports searching for words and terms with flexible wildcard patterns, retrieving detailed information about lexemes, meanings, word paradigms, and source references

<details>
<summary><strong>The API facilitates:</strong></summary>

* **Lexical Data Access:** Search and retrieve comprehensive information about Estonian words, including their forms, definitions, usage examples, and linguistic properties
* **Terminology Management:** Access specialized terminology from over 150 domain-specific dictionaries and terminology databases
* **Multilingual data:** Datasets in Ekilex database include entries in multiple languages incl. english, russian, latvian, norwegian etc.
* **Linguistic Research:** Query morphological, etymological, and semantic relationships between words
* **Application Integration:** Enable third-party applications, language tools, and educational platforms to incorporate Estonian language data

The data managed through Ekilex is publicly accessible via **[Sõnaveeb](https://sonaveeb.ee) (WordWeb)**, Estonia's primary language portal.

---
**Intended Audience**

The Ekilex API is intended for:
- **Lexicographers and terminologists** working on Estonian dictionaries and terminology databases
- **Linguists and researchers** conducting computational linguistics studies
- **Institutional partners** requiring programmatic access to authoritative Estonian language resources
- **Software developers** integrating Estonian language data into applications and digital tools

  Developer use cases:
    - **Application integration**: Connect lexical resources to dictionaries, spell checkers, learning applications, translation systems, or text-processing tools
    - **NLP tasks**: Use data for morphological analysis, lemmatization, and semantic search
    - **Custom tool development**: Build applications for linguistic research or vocabulary training
    - **Automation**: Programmatically retrieve, update, and synchronize lexical data
    - **Open-source collaboration**: Extend functionality or adapt the API via the public codebase

Each user receives a unique API key linked to their Ekilex account, with permissions aligned to their role and dataset access rights.
</details>
---
<details>
<summary><strong>Authentication & Authorization</strong></summary>

### API Key Requirements

**Obtaining an API Key:**
1. Register and log in to your Ekilex user account at [ekilex.ee](https://ekilex.ee)
2. Navigate to your user profile page
3. Generate an API key (only one key per user; generating a new key replaces the existing one)

**Authentication Header:**  
Example:

<code>ekilex-api-key: cd8c61505b17423282550623464fcace</code>

Include this header in all API requests.  
Since each API key is associated with a particular user, all data modification actions are logged by that name.

---

### Permission Model
* **Read Operations:** All authenticated users can query and retrieve data
* **CRUD Operations:** Creating, updating, and deleting data requires special permissions:
    * Admin rights, **or**
    * CRUD permissions granted by an administrator via the Ekilex permissions page
* **Dataset Permissions:** The same permission logic applies as in the Ekilex web interface
* **Action Logging:** All modifications are logged under the API key owner's username
</details>
---

<details>
<summary><strong>Common Error Responses</strong></summary>

The API returns structured error responses in JSON format. Below are the common error scenarios you may encounter:

**400 - Bad Request**  
The request contains invalid parameters or malformed input.


**403 - Forbidden**  
Authentication failed or user lacks permission to access the requested resource.


**404 - Not Found**  
The requested resource does not exist in the database.

**Error Handling Best Practices**
1. **Always check the HTTP status code** - Use it to determine error category
2. **Use the error `code` field** - Machine-readable codes enable programmatic error handling
3. **Log the `timestamp` and `path`** - Helps with debugging and support requests
4. **Handle 403 errors gracefully** - Check API key validity and user permissions:
    * Missing `ekilex-api-key` header → Add authentication header
    * Invalid API key → Verify key format and regenerate if necessary
    * Insufficient permissions → Contact administrator for CRUD role grant
5. **Retry strategy for transient errors** - 5xx errors are server issues and may be retried  
</details>
---
