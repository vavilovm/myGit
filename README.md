# Git

Cистема контроля версий и CLI к ней со следующими возможностями:

* `init` -- инициализация репозитория
* `add <files>` -- добавление файла
* `rm <files>` -- файл удаляется из репозитория, физически остается
* `status` -- измененные/удаленные/не добавленные файлы
* `commit <message>` с проставлением даты и времени
* `reset <to_revision>`. Поведение `reset` совпадает с `git reset --hard`
* `log [from_revision]`
* `checkout <revision>`
    * Возможные значения `revision`:
        * `commit hash` -- хеш коммита
        * `master` -- вернуть ветку в исходное состояние
        * `HEAD~N`, где `N` -- неотрицательное целое число. `HEAD~N` означает _N-й коммит перед HEAD (`HEAD~0 == HEAD`)
* `checkout -- <files>` -- сбрасывает изменения в файлах
* `branch-create <branch>` -- создать ветку с именем `<branch>`
* `branch-remove <branch>` -- удалить ветку `<branch>`
* `show-branches` -- показать все имеющиеся ветки
* `merge <branch>` -- слить ветку `<branch>` в текущую

## Примечания

* `<smth>` означает, что передаваемые данные обязательны
* `[smth]` означает, что передаваемые данные опциональны