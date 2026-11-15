# Лаборатори 10: Өгөгдлийн урсгалын анализ

Энэ лабораторид `gcd` болон `bonus` функцийн хувьсагчийн тодорхойлолт (definition, **def**) болон хэрэглээ (use)-г шинжилж, def-clear замтай DU хос бүрийг жагсаав. JaCoCo-ийн branch хэмжүүр DU-хосын хамралт биш гэдгийг тест болон мутацийн үр дүнгээр харуулна.

> **Мөрийн дугаар:** Энэ тайлангийн дугаарууд [`DataFlowTargets.java`](src/main/java/mn/edu/must/sqat/DataFlowTargets.java) файлын дугаар. Эцсийн эх код starter-тэй ижил.

## Орчин, оюутны мэдээлэл

Төслийн Maven тохиргоо Java 17 target-тай. Тестийг Windows 11 дээр Java 22, Maven 3.10.0-аар ажиллуулав. Командын гаралт:

```text
> java -version
java version "22.0.2" 2024-07-16
Java(TM) SE Runtime Environment (build 22.0.2+9-70)
Java HotSpot(TM) 64-Bit Server VM (build 22.0.2+9-70, mixed mode, sharing)

> mvn -version
Apache Maven 3.10.0 (c43a36b8d67be7e0805a411bc0898af1a51f5472)
Maven home: C:\Tools\apache-maven-3.10.0
Java version: 22.0.2, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-22
Default locale: en_US, platform encoding: UTF-8, time zone: Asia/Ulaanbaatar
OS name: "windows 11", version: "10.0", arch: "amd64"
```

**Оюутны нэр:** Г. Саранчимэг

**Оюутны код:** B232274001

AI туслахыг DU хүснэгт, тест, тайлангийн ноорог бэлтгэх болон шалгалтад ашигласан; мутацийн үр дүнг төслийн кодон дээр Maven-аар ажиллуулж бүртгэв.

## A. `gcd` — бүрэн DU хосууд

Файлын мөрийн зураглал: 13-р мөрд `x`, `y` параметрийн def; 15-р мөрд `y`-ийн p-use; 16-р мөрд `x`, `y`-ийн c-use болон `tmp`-ийн def; 17-р мөрд `y`-ийн c-use болон `x`-ийн def; 18-р мөрд `tmp`-ийн c-use болон `y`-ийн def; 20-р мөрд `x`-ийн c-use байна. `tmp`-ийн 14-р мөрийн зарлал эхлүүлэлтгүй тул def биш.

| Хувьсагч | Def мөр | Use мөр | Use төрөл / салаа | Def-clear зам | Тестийн ID | Хамрагдсан |
|---|---:|---:|---|---|---|---|
| `x` | 13 | 16 | c-use | 13 → 15(T) → 16 | GCD-02, GCD-03 | Тийм |
| `x` | 13 | 20 | c-use | 13 → 15(F) → 20 | GCD-01 | Тийм |
| `x` | 17 | 16 | c-use | 17 → 18 → 15(T) → 16 | GCD-02 | Тийм |
| `x` | 17 | 20 | c-use | 17 → 18 → 15(F) → 20 | GCD-02, GCD-03 | Тийм |
| `y` | 13 | 15 | p-use, T | 13 → 15(T) | GCD-02, GCD-03 | Тийм |
| `y` | 13 | 15 | p-use, F | 13 → 15(F) | GCD-01 | Тийм |
| `y` | 13 | 16 | c-use | 13 → 15(T) → 16 | GCD-02, GCD-03 | Тийм |
| `y` | 13 | 17 | c-use | 13 → 15(T) → 16 → 17 | GCD-02, GCD-03 | Тийм |
| `y` | 18 | 15 | p-use, T | 18 → 15(T) | GCD-02 | Тийм |
| `y` | 18 | 15 | p-use, F | 18 → 15(F) | GCD-02, GCD-03 | Тийм |
| `y` | 18 | 16 | c-use | 18 → 15(T) → 16 | GCD-02 | Тийм |
| `y` | 18 | 17 | c-use | 18 → 15(T) → 16 → 17 | GCD-02 | Тийм |
| `tmp` | 16 | 18 | c-use | 16 → 17 → 18 | GCD-02, GCD-03 | Тийм |

[`GcdTest.java`](src/test/java/mn/edu/must/sqat/GcdTest.java)-ийн тестүүд:

- **GCD-01 — `gcd(7, 0) = 7`:** давталт огт ажиллахгүй. `x@13 → 20` болон `y@13 → 15(F)` хосыг ганцаараа хамарна.
- **GCD-02 — `gcd(12, 8) = 4`:** давталтын бие хоёр удаа ажиллана. Loop доторх шинэ `x`, `y`, `tmp` def-үүд болон давталтын T/F замуудыг хамарна.
- **GCD-03 — `gcd(0, 5) = 5`:** бие нэг удаа ажиллаад давталтаас гарна; `x@17 → 20`-ийг мөн хамарна.

## B. `bonus` — бүрэн анализ

### B1. CFG

[`cfg-bonus.png`](cfg-bonus.png) зурагт эх кодын мөрөөр дугаарласан зангилаанууд, for-each-ийн iterator def ба attendance p-use, салаануудыг дайрах тестийн method нэрсийг тэмдэглэв. For-each-ийн нөхцөл нь дараагийн элемент байгаа эсэх; iteration алхам нь дараагийн элементийг `a` хувьсагчид оноох үйлдэл.

### B2. Def/use жагсаалт

Predicate-д оролцсон хувьсагчдыг p-use гэж ангилсан; predicate доторхыг давхар c-use гэж тоолоогүй.

| Мөр | Def | Use |
|---:|---|---|
| 31 | `attendance`, `threshold` параметрүүд | — |
| 32 | — | `attendance == null` p-use; `threshold < 0` p-use (зөвхөн attendance null биш үед short-circuit-ээр шалгана) |
| 33 | — | Хувьсагчийн use байхгүй |
| 35 | `present = 0` | — |
| 36 | `streak = 0` | — |
| 37 | `best = 0` | — |
| 38 | `a`-д дараагийн элементийг оноох def | `attendance`-д дараагийн элемент байгаа эсэх p-use |
| 39 | — | `a > 0` p-use |
| 40 | `present` | `present + 1` c-use |
| 41 | `streak` | `streak + 1` c-use |
| 42 | — | `streak > best` p-use (`streak`, `best`) |
| 43 | `best` | `streak` c-use |
| 46 | `streak = 0` | — |
| 49 | `score = 0` | — |
| 50 | — | `present >= threshold` p-use (`present`, `threshold`) |
| 51 | `score = 5` | — |
| 53 | — | `best >= 5` p-use (`best`) |
| 54 | `score` | `score + 3` c-use |
| 56 | — | `score` c-use |

### B3. DU хүснэгт

Хүснэгт дэх Тестийн ID нь [`BonusTest.java`](src/test/java/mn/edu/must/sqat/BonusTest.java)-ийн `@DisplayName`-тай таарна. `Боломжгүй` гэдэг нь хүснэгтэд дурдсан def-ээс тухайн use-д хүрэхийн өмнө заавал дахин def хийгдэж kill болох, эсвэл predicate-ийн утга хүрэх боломжгүйг хэлнэ.

| Хувьсагч | Def | Use | Төрөл / салаа | Def-clear замын нөхцөл | Тестийн ID | Хамрагдсан |
|---|---:|---:|---|---|---|---|
| `attendance` | 31 | 32 | p-use, null=true | null аргумент | BONUS-06 | Тийм |
| `attendance` | 31 | 32 | p-use, null=false | null биш аргумент | BONUS-01–05, 07–09 | Тийм |
| `attendance` | 31 | 38 | p-use, элемент байна | хоосон биш, хүчинтэй массив | BONUS-01–05, 07, 09 | Тийм |
| `attendance` | 31 | 38 | p-use, дууссан | массивын төгсгөл | BONUS-01–05, 07–09 | Тийм |
| `threshold` | 31 | 32 | p-use, `< 0` true | attendance null биш, сөрөг threshold | BONUS-06 | Тийм |
| `threshold` | 31 | 32 | p-use, `< 0` false | attendance null биш, сөрөг биш threshold | BONUS-01–05, 07–09 | Тийм |
| `threshold` | 31 | 50 | p-use, true | ирцийн тоо threshold-д хүрнэ | BONUS-01, 08 | Тийм |
| `threshold` | 31 | 50 | p-use, false | ирцийн тоо threshold-д хүрэхгүй | BONUS-02–05, 07, 09 | Тийм |
| `a` | 38 | 39 | p-use, `a > 0` true | эерэг ирцийн өдөр | BONUS-01, 02, 04, 07, 09 | Тийм |
| `a` | 38 | 39 | p-use, `a > 0` false | тэг ирцийн өдөр | BONUS-01, 03–05, 07, 09 | Тийм |
| `present` | 35 | 40 | c-use | эхний эерэг өдрийг тоолох | BONUS-01, 02, 04, 07, 09 | Тийм |
| `present` | 35 | 50 | p-use, true | хоосон ирц, threshold=0 | BONUS-08 | Тийм |
| `present` | 35 | 50 | p-use, false | эерэг өдөргүй, threshold>0 | BONUS-03 | Тийм |
| `present` | 40 | 40 | c-use | дараагийн эерэг өдрийг тоолох | BONUS-01, 02, 07, 09 | Тийм |
| `present` | 40 | 50 | p-use, true | нийт эерэг өдөр threshold-д хүрэх | BONUS-01 | Тийм |
| `present` | 40 | 50 | p-use, false | нийт эерэг өдөр threshold-ээс бага | BONUS-02, 04, 05, 07, 09 | Тийм |
| `streak` | 36 | 41 | c-use | эхний эерэг өдөрт streak-ийг нэмэх | BONUS-01, 02, 04, 07, 09 | Тийм |
| `streak` | 36 | 42 | p-use | 41-р мөр эхлээд streak-ийг дахин тодорхойлно | Ямар ч боломжит замгүй | Боломжгүй |
| `streak` | 36 | 43 | c-use | 42-р мөр хүрэхээс өмнө 41-р мөр def хийнэ | Ямар ч боломжит замгүй | Боломжгүй |
| `streak` | 41 | 41 | c-use | хоёр эерэг өдөр дараалж ирэх | BONUS-01, 02, 07, 09 | Тийм |
| `streak` | 41 | 42 | p-use, true | streak өмнөх best-ээс урт | BONUS-01, 02, 07, 09 | Тийм |
| `streak` | 41 | 42 | p-use, false | best-д хүрэхгүй шинэ streak | BONUS-01, 05, 07, 09 | Тийм |
| `streak` | 41 | 43 | c-use | 42-р мөр true салаагаар best шинэчлэх | BONUS-01, 02, 07, 09 | Тийм |
| `streak` | 46 | 41 | c-use | тасалдлын дараах эерэг өдөр | BONUS-01, 04 | Тийм |
| `streak` | 46 | 42 | p-use | дараагийн iteration-д 41-р мөр түрүүлж def хийнэ | Ямар ч боломжит замгүй | Боломжгүй |
| `streak` | 46 | 43 | c-use | 41-р мөр эхлээд streak-ийг дахин def хийнэ | Ямар ч боломжит замгүй | Боломжгүй |
| `best` | 37 | 42 | p-use, true | эхний эерэг өдөр streak>0 | BONUS-01, 02, 04, 07 | Тийм |
| `best` | 37 | 42 | p-use, false | анхны best=0 үед шалгах streak заавал 1-ээс их | Биелэхгүй | Боломжгүй |
| `best` | 37 | 53 | p-use, false | best шинэчлэгдэхгүй 0 хэвээр | BONUS-03, 08 | Тийм |
| `best` | 37 | 53 | p-use, true | best>=5 болохоос өмнө 43-р мөр заавал def хийнэ | Биелэхгүй | Боломжгүй |
| `best` | 43 | 42 | p-use, true | өмнөх best-ээс урт дараагийн streak | BONUS-01, 09 | Тийм |
| `best` | 43 | 42 | p-use, false | best хадгалагдсан, дараагийн streak богино/тэнцүү | BONUS-01, 09 | Тийм |
| `best` | 43 | 53 | p-use, true | хамгийн урт цуваа 5 буюу түүнээс урт | BONUS-01, 02 | Тийм |
| `best` | 43 | 53 | p-use, false | best шинэчлэгдсэн ч хамгийн урт цуваа 5-аас богино | BONUS-05, 07, 09 | Тийм |
| `score` | 49 | 54 | c-use | threshold false, best>=5 | BONUS-02 | Тийм |
| `score` | 49 | 56 | c-use | threshold false, best<5 | BONUS-03–05, 07, 09 | Тийм |
| `score` | 51 | 54 | c-use | threshold true, best>=5 | BONUS-01 | Тийм |
| `score` | 51 | 56 | c-use | threshold true, best<5 | BONUS-08 | Тийм |
| `score` | 54 | 56 | c-use | нэмэлт оноог тооцоод буцаах | BONUS-01, 02 | Тийм |

46-р мөрийн `streak = 0` нь 41-р мөр хүртэл хүрэх хуучин `streak` def-үүдийг kill хийнэ. Гэхдээ 46-р мөрийн **шинэ** def дараагийн эерэг өдрийн 41-р мөрийн баруун талын c-use-д хүрч болно. 46-р мөрөөс 42-р мөрийн p-use эсвэл 43-р мөрийн c-use хүртэлх def-clear зам байхгүй: for-each-ийн дараагийн iteration-д 41-р мөр `streak`-ийг дахин тодорхойлно; дахин сөрөг элемент байвал 46-р мөрөөр дахин kill болно. 36-р мөрийн def 42-т хүрэхгүй, учир нь 42 шалгагдахаас өмнө 41-р мөр заавал `streak`-ийг дахин тодорхойлно.

37-р мөрийн `best=0` def 42-р мөрийн true салаанд хүрч болох ч false салаанд хүрэх боломжгүй: 42-р мөрд хүрэхэд `streak` 0-ээс их болсон байна. Тэр def 53-р мөрийн false салаанд хүрч болно (best 0 хэвээр), харин true салаанд хүрэхээс өмнө 43-р мөр `best`-ийг def хийнэ. Эдгээр нь def-clear бус хослолыг хүснэгтэд DU хос гэж тоолохгүй байх жишээнүүд.

### B4. All-defs ба all-DU-pairs тест

All-defs-ийн хувьд `bonus`-ийн хамгийн цөөн **хоёр** гүйцэтгэл шаардагдана. Нэг гүйцэтгэлд `present >= threshold` true байж байж `score=5` (51-р мөр) def үүснэ; харин `score=0` (49-р мөр)-ийн def use-д хүрэхийн тулд threshold нөхцөл false байх ёстой. Эдгээр нөхцөл нэг гүйцэтгэлд зэрэг биелэхгүй. Эхний шатанд `BonusTest`-д зөвхөн BONUS-01 ба BONUS-02 all-defs тест байв; бусад гурван тест нь `gcd`-ийн тест тул тухайн `mvn verify` нийт 5 тест ажиллуулсан.

- [`results/mvn-verify-alldefs.txt`](results/mvn-verify-alldefs.txt): `Tests run: 5, Failures: 0, Errors: 0, Skipped: 0`; `BUILD SUCCESS`.
- [`results/jacoco-alldefs.csv`](results/jacoco-alldefs.csv): `DataFlowTargets`-ийн branch **13/16** (3 missed), line **23/25** (2 missed). All-defs хангагдсан ч branch 100% биш байж болно.
- Дараагийн шатанд BONUS-03–09-г нэмж DU-хосын зам, биелэх боломжтой predicate салаанууд болон M2-ийг унагах тестийг оруулсан.
- Эцсийн `mvn verify` нийт **12 тест** ажиллуулж, бүгдийг амжилттай дуусгав.

### B5. JaCoCo эцсийн үзүүлэлт

[`results/jacoco-final.csv`](results/jacoco-final.csv)-ийн `DataFlowTargets` мөр:

| Үзүүлэлт | Утга |
|---|---:|
| Branch missed / covered | 0 / 16 — **16/16, 100%** |
| Line missed / covered | 1 / 24 — **24/25** |
| Instruction missed / covered | 3 / 69 |

Missed line нь 7-р мөрийн Java далд constructor. Үүнийг хамруулахын тулд `new DataFlowTargets()` тест нэмэх эсвэл эх кодыг өөрчлөх хэрэггүй. Branch 100% болсон ч DU хос дутуу байж болохыг туршилтаар үзүүлэв: BONUS-08-ийг түр хассан үед [`results/jacoco-branch-vs-du.csv`](results/jacoco-branch-vs-du.csv)-д branch **16/16** хэвээр боловч `score@51 → 56` (threshold үнэн, best<5) хос хамрагдаагүй; тэр зам зөвхөн threshold true, best false үед хэрэгжинэ. Энэ сонголтын туршилтын бүртгэл [`results/mvn-verify-branch-vs-du.txt`](results/mvn-verify-branch-vs-du.txt)-д байна (12 test method-оос 1 skipped). Эцсийн багцад BONUS-08-ийг буцаан идэвхжүүлж уг хосыг хамруулсан. Иймээс branch 100% нь all-DU-pairs гэсэн баталгаа биш.

## C. Fault seeding — үр дүн

Мутаци бүрийг `DataFlowTargets.java` дээр нэг нэгээр нь хийж `mvn test` ажиллуулаад, дараа нь `git restore`-оор эх кодыг сэргээв. Лог файлууд `results/` дотор хадгалагдсан.

| Мутаци | Өөрчлөлт | Туршилтын үр дүн |
|---|---|---|
| M1 | 41-р мөрийн `streak = streak + 1`-ийг устгасан | [`mvn-test-M1.txt`](results/mvn-test-M1.txt): нийт 9 тестээс 2 унасан. `BonusTest.allDefsScoreZeroReachesUse`: expected `<3>`, was `<0>`; `BonusTest.allDefsThresholdAndStreakTrue`: expected `<8>`, was `<5>`. |
| M2 | 43-р мөрийн `best = streak`-ийг `best = present` болгосон | [`mvn-test-M2.txt`](results/mvn-test-M2.txt): нийт 9 тест, 0 уналт — мутаци анхны DU багцад амьд үлдсэн. |
| M2-kill | Дараа нь 4 тасархай ирцийн дараа хоёр дараалсан ирцтэй тест нэмсэн | [`mvn-test-M2-kill.txt`](results/mvn-test-M2-kill.txt): нийт 10 тестээс `BonusTest.separatedPresencesKillWrongBestDefinition` унасан; expected `<0>`, was `<3>`. |
| M3 | Давталтын дараа ашиглагдахгүй `int total = present + best;` нэмсэн | [`mvn-test-M3.txt`](results/mvn-test-M3.txt): нийт 10 тест, 0 уналт; `javac` анхааруулга гаргаагүй. |

M2 эхний багцаар баригдаагүй шалтгаан: `best` шинэчлэгдэх нөхцөл биелсэн эхний цуваануудад `present` ба `streak` ижил байсан. Best=5 болсны дараах reset-ын дараа богино streak нь нөхцөлийг true болгохгүй, тиймээс буруу def үүссэнгүй. M2-kill тестэд тав дахь эерэг өдрийг дөрвөн тасархай өдрийн дараа хоёр дахь дараалсан өдрөөр өгсөн: тэр үед `present=5`, `streak=2`, өмнөх best=1. Зөв код best-ийг 2 болгож 0 оноо буцаана; мутант best-ийг 5 болгож 3 оноо буцаасан.

M3 нь dead definition: `total`-ийн утга дараа нь огт уншигдахгүй. Үүнийг runtime тест барихгүй; IDE-ийн unused-variable inspection эсвэл статик анализатор илрүүлэх ёстой. Энэ орчны VS Code Problems шалгалт алдаа мэдээлээгүй бөгөөд `mvn test` ч амжилттай байсан тул багшийн хүссэн IDE warning-ийн мөрийг энд зохиож хуулсангүй.

## AI хүснэгт ба өөрийн хүснэгтийн харьцуулалт

Эхний AI-ийн ноорог хүснэгт бүрэн байгаагүй: `score@49 → 56` болон `score@51 → 56` хос, мөн 46-р мөрийн reset-ийн дараах зарим p-use салааг орхисон. Тэр ноорог line 50 дахь `present`, `threshold`-ийг c-use гэж тэмдэглэсэн нь буруу; эдгээр нь predicate-д оролцож байгаа тул p-use гэж тоолно. `streak@46 → 43` хосыг шууд боломжтой гэж үзвэл мөн алдаа болно, учир нь line 41 тухайн `streak`-ийг эхэлж дахин def хийдэг. `best@37 → 42(false)` ба `best@37 → 53(true)` хосуудын хувьд def-clear зам байхгүйг тусад нь үндэслэв. Хүснэгтийн мөр бүрийг кодын def/use байрлал, kill болон тестийн бодит өгөгдөлтэй тулгаж зассан.

## Дүгнэлт

DU анализ нь салааг шалгасан эсэхээс гадна нэг def яг ямар use-д kill-гүй хүрснийг тогтоодог тул branch coverage-оос илүү нарийвчилсан. Үүний төлөө def/use бүрийг замаар нь гараар хянах шаардлагатай учир DU хүснэгт гаргах ажиллагаа илүү үнэтэй. `bonus`-ийн давталтад `streak` ба `best` өөр өөр үед шинэчлэгддэг нь хамгийн их анхаарал шаардсан хэсэг байв. JaCoCo 16 салааг бүгдийг хамарсан ч DU хосын хүснэгтийг орлохгүй. All-defs-д score-ийн эсрэг нөхцөлүүдээс шалтгаалан дор хаяж хоёр тусдаа гүйцэтгэл хэрэгтэй байлаа. M1 тестүүдэд шууд баригдсан, харин M2-г илрүүлэхийн тулд оролтын утгуудын ялгааг зориуд тодруулсан тест шаардсан. M3 шиг ашиглагдахгүй def-ийг зөвхөн runtime тестээр барих боломжгүй.

## Шалгалт ба GitHub

```powershell
mvn verify
```

Эцсийн гаралт [`results/mvn-verify-final.txt`](results/mvn-verify-final.txt), CSV нь [`results/jacoco-final.csv`](results/jacoco-final.csv). JaCoCo HTML тайлан `target/site/jacoco/index.html`-д гарна; `target/` `.gitignore`-д хасагдсан.


