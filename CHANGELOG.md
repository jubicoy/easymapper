# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
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
