# Web-Based Video Browsing System for Special Events

SE2030 Software Engineering Group Project - SLIIT, Year 2 Semester 1 2026

## Team & Module Ownership

| Member          | Module                              | Package                                   |
|-----------------|--------------------------------------|--------------------------------------------|
| Vaanushan V     | Admin Panel                          | `com.sliit.videobrowsing.admin`            |
| Asven           | Video Management                     | `com.sliit.videobrowsing.video`            |
| Amsakan         | Search, Browsing & Notification      | `com.sliit.videobrowsing.search`           |
| Sandeepani      | Playlist Management                  | `com.sliit.videobrowsing.playlist`         |
| Maldeniya       | Playback & User Management           | `com.sliit.videobrowsing.user`, `security` |
| Pathirana       | Comments & Reviews Management        | `com.sliit.videobrowsing.comment`          |

## Tech Stack
- Java 17, Spring Boot 3.3
- Spring Data JPA + MySQL
- Spring Security + JWT (jjwt)
- AWS S3 (video/thumbnail storage)
- Maven

## Getting Started

1. Install MySQL locally and create a schema (or let `application.yml`'s
   `createDatabaseIfNotExist=true` do it for you).
2. Copy `src/main/resources/application.yml` values for your local DB
   username/password. **Do not commit real credentials** — for real
   secrets, override via environment variables or a local
   `application-local.yml` (already gitignored).
3. Run:
   ```bash
   mvn spring-boot:run
   ```
4. API runs at `http://localhost:8080/api/...`

## Package Structure
Each of the 6 major functions lives in its own package under
`com.sliit.videobrowsing`, each with its own `entity`, `repository`,
`service`, and `controller` sub-packages:

```
com.sliit.videobrowsing
├── common/        shared BaseEntity, exceptions, JPA config
├── security/       JWT filter, util, Spring Security config
├── user/           Playback & User Management   (Maldeniya)
├── video/           Video Management              (Asven)
├── playlist/         Playlist Management            (Sandeepani)
├── search/           Search, Browsing & Notification (Amsakan)
├── comment/          Comments & Reviews Management  (Pathirana)
└── admin/            Admin Panel                    (Vaanushan V)
```

## Branching Convention
- `main` — protected, only updated via reviewed PRs
- `feature/<module-name>` — e.g. `feature/video-management`, one per member
- Open a PR into `main` when a feature is demo-ready; tag a teammate to review

## Notes
- `ddl-auto: update` auto-creates tables from the JPA entities on first run —
  no manual SQL needed for the base schema.
- AWS S3 upload logic is stubbed in `VideoController`/`VideoService` — wire up
  an `S3Client` bean using the `aws.s3.*` properties before Week 10.
- JWT secret in `application.yml` is a placeholder — replace before any real deployment.
