# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.5.8] - 2024-08-31
### Changed
- Bump fi.jubic:easyvalue from 1.4.1 to 1.4.2.
- Bump fi.jubic:easyparent from 0.1.12 to 0.1.13.

## [0.5.7] - 2024-05-06
### Changed
- Bump fi.jubic:easyparent from 0.1.11 to 0.1.12.
- Bump fi.jubic:easyvalue from 1.4.0 to 1.4.1.

## [0.5.6] - 2023-12-13
### Added
- Java 21 tests.

### Changed
- Bump fi.jubic:easyparent from 0.1.10 to 0.1.11.
- Bump fi.jubic:easyvalue from 1.3.8 to 1.4.0.
- Bump auto-service from 1.0.1 to 1.1.1.

## [0.5.5] - 2023-03-14
### Changed
- Upgrade easyvalue to 1.3.8.

## [0.5.4] - 2023-03-09
### Security
- Update parent and dependencies.

## [0.5.3] - 2022-06-19
### Security
- Update parent and dependencies.

## [0.5.2] - 2021-12-13
### Fixed
- Updated easyvalue due to a bug in easyvalue 1.3.5.

## [0.5.1] - 2021-12-10
### Added
- Full Java 17 support.

## [0.5.0] - 2021-04-20
### Changed
- Upgrade easymapper-jooq to JOOQ 3.14.4.

### Fixed
- Fix aliasing with JOOQ 3.14.x.

## [0.4.3] - 2020-10-27
### Changed
- Skip property assignment when using a `NoOpAccessor`.

## [0.4.2] - 2020-07-24
### Fixed
- Restore `Mapper::mapOptional`.

## [0.4.1] - 2020-07-24
### Fixed
- Non-nullable references could not be collected.

## [0.4.0] - 2020-07-20
### Added
- `withoutX` JOOQ mapper builder method for ignoring properties.

### Fixed
- Fix `NullPointerException` when joined sub-collection is empty.

## [0.3.0] - 2020-03-24
### Added
- Generate `collectingWith` and `collectingWithMany` for references to allow
collecting collection references through another reference.

### Changed
- Return implementation type from `RecordMapper::alias` in generated mappers.

## [0.2.0] - 2020-03-23
### Changed
- Use only `@EasyId` annotation to generate mappers.

### Fixed
- Filter out null values from collection references.

## [0.1.0] - 2020-02-05

Initial release.
