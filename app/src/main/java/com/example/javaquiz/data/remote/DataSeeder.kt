package com.example.javaquiz.data.remote

import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataSeeder {

    private val questionsData = listOf(
        SeedQuestion("looping", "Berapa kali output 'Java' dicetak?\nfor(int i=0; i<5; i++) { System.out.println(\"Java\"); }", "4 kali", "5 kali", "6 kali", "10 kali", "B", "Perulangan dari i=0 sampai i<5 (i=0,1,2,3,4) → 5 kali cetak."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=1; i<=3; i++) { System.out.print(i + \" \"); }", "0 1 2", "1 2 3", "1 2 3 4", "0 1 2 3", "B", "i=1,2,3 karena kondisi i<=3, cetak 1 2 3."),
        SeedQuestion("looping", "Kata kunci untuk keluar dari perulangan secara paksa adalah?", "exit", "stop", "break", "return", "C", "'break' digunakan untuk menghentikan perulangan lebih awal."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=0; i<5; i++) { if(i==3) break; System.out.print(i); }", "0123", "012", "01234", "34", "B", "i=0,1,2 cetak. Saat i=3, break berhenti. Output: 012."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=0; i<5; i++) { if(i==2) continue; System.out.print(i); }", "01234", "0134", "0234", "0124", "B", "continue melewati i=2, jadi cetak 0,1,3,4 → 0134."),
        SeedQuestion("looping", "Perulangan while akan dieksekusi selama kondisi bernilai?", "false", "true", "0", "null", "B", "While loop terus berjalan selama kondisi bernilai true."),
        SeedQuestion("looping", "Apa output dari?\nint x=3; while(x>0) { System.out.print(x--); }", "321", "123", "32", "21", "A", "x=3 cetak 3 lalu decrement, x=2 cetak 2, x=1 cetak 1, x=0 stop. Output: 321."),
        SeedQuestion("looping", "Apa perbedaan do-while dengan while?", "do-while tidak pakai kurung", "do-while dieksekusi minimal 1 kali meskipun kondisi false", "do-while hanya untuk angka", "do-while tidak bisa pakai break", "B", "do-while menjalankan blok kode dulu, baru cek kondisi. Minimal 1x eksekusi."),
        SeedQuestion("looping", "Apa output dari?\nint i=0; do { System.out.print(i); i++; } while(i<3);", "012", "0123", "12", "01", "A", "Cetak 0,1,2 lalu i=3 → kondisi i<3 false, berhenti. Output: 012."),
        SeedQuestion("looping", "Manakah perulangan yang tepat untuk mencetak angka genap 0,2,4,6,8?", "for(int i=0; i<=8; i+=2)", "for(int i=0; i<8; i++)", "for(int i=0; i<=8; i++)", "for(int i=2; i<8; i+=2)", "A", "i+=2 melompat 2 langkah: 0,2,4,6,8. Kondisi i<=8."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=1; i<=3; i++) { for(int j=1; j<=2; j++) { System.out.print(\"*\"); } }", "***", "******", "*****", "**", "B", "Outer loop 3x, inner loop 2x, total 3×2=6 bintang: ******."),
        SeedQuestion("looping", "Kata kunci 'continue' pada perulangan berfungsi untuk?", "Menghentikan perulangan", "Melanjutkan ke iterasi berikutnya", "Mengulang dari awal", "Menghentikan program", "B", "continue melewati sisa kode di iterasi saat ini dan lanjut ke iterasi berikutnya."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=0; i<3; i++) { System.out.print((i+1)+\" \"); }", "1 2 3", "0 1 2", "0 1 2 3", "1 2 3 4", "A", "i=0 cetak 1, i=1 cetak 2, i=2 cetak 3. Output: 1 2 3."),
        SeedQuestion("looping", "Perulangan for-each digunakan untuk?", "Perulangan dengan counter", "Iterasi elemen dalam array/collection", "Perulangan tanpa kondisi", "Perulangan tak terbatas", "B", "for-each (enhanced for loop) digunakan untuk iterasi setiap elemen array/collection."),
        SeedQuestion("looping", "Apa output dari?\nint a=0; for(; a<3; a++) { } System.out.print(a);", "3", "2", "4", "0", "A", "a=0,1,2 (loop). Setelah loop a=3, lalu cetak 3."),
        SeedQuestion("looping", "Manakah loop yang tepat untuk iterasi array?\nint[] arr = {1,2,3};", "for(int i: arr)", "for(i=0; i<arr.length(); i++)", "for(int i=0; i<=arr.length; i++)", "for each(i in arr)", "A", "for-each: for(int i : arr) {...}. Catatan: arr.length tanpa () untuk array."),
        SeedQuestion("looping", "Apa output dari?\nint i=5; while(i>0) { i--; } System.out.print(i);", "5", "1", "0", "-1", "C", "i=5→4→3→2→1→0, saat i=0 loop berhenti, cetak 0."),
        SeedQuestion("looping", "Berapa kali perulangan berikut?\nfor(int i=10; i>5; i--)", "4 kali", "5 kali", "6 kali", "10 kali", "B", "i=10,9,8,7,6 → i>5. i=10,9,8,7,6 = 5 kali."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=0; i<10; i+=3) { System.out.print(i); }", "0369", "036", "036912", "369", "A", "i=0,3,6,9 (karena 9<10, 12 sudah >10). Output: 0369."),
        SeedQuestion("looping", "Manakah yang BUKAN merupakan jenis perulangan di Java?", "for", "while", "do-while", "repeat-until", "D", "Java memiliki for, while, do-while. 'repeat-until' tidak ada di Java."),
        SeedQuestion("looping", "Apa output dari?\nint x=1; while(++x<5) { } System.out.print(x);", "4", "5", "6", "1", "B", "++x increment dulu: x=2<5, 3<5, 4<5, 5<5 false. x=5 dicetak."),
        SeedQuestion("looping", "Nested loop adalah?", "Loop dalam loop", "Loop tanpa kondisi", "Loop dengan break", "Loop dengan continue", "A", "Nested loop adalah perulangan di dalam perulangan (loop bersarang)."),
        SeedQuestion("looping", "Apa output dari?\nfor(int i=0; i<2; i++) { for(int j=0; j<2; j++) { System.out.print(i+j); } }", "0123", "0012", "0101", "1234", "A", "i=0,j=0→0; i=0,j=1→1; i=1,j=0→1; i=1,j=1→2. Output: 0123."),
        SeedQuestion("looping", "Jika kita ingin mengulang kode 100 kali, manakah yang PALING efisien?", "Menulis kode 100 kali", "Menggunakan for loop", "Menggunakan if-else", "Menggunakan switch-case", "B", "For loop adalah cara paling efisien untuk mengulang kode dengan jumlah iterasi yang diketahui."),
        SeedQuestion("looping", "Apa yang terjadi jika kondisi dalam while selalu true?", "Program error", "Infinite loop", "Loop berjalan sekali", "Loop dilewati", "B", "Jika kondisi selalu true, loop tidak akan pernah berhenti (infinite loop)."),

        SeedQuestion("inheritance", "Kata kunci untuk mewarisi class di Java adalah?", "implements", "extends", "inherits", "super", "B", "'extends' digunakan untuk inheritance class di Java."),
        SeedQuestion("inheritance", "Manakah yang benar tentang inheritance di Java?", "Satu class bisa extends multiple class", "Satu class hanya bisa extends satu parent class", "Inheritance tidak bisa dipakai di Java", "Semua class otomatis extends Object", "B", "Java hanya mendukung single inheritance: satu child class hanya bisa extends satu parent class."),
        SeedQuestion("inheritance", "Apa output dari?\nclass A { int x=5; }\nclass B extends A { int x=10; }\nB b = new B(); System.out.print(b.x);", "5", "10", "15", "Error", "B", "Variabel di shadow oleh child class. b.x mengacu ke x di class B yaitu 10."),
        SeedQuestion("inheritance", "Kata kunci 'super' digunakan untuk?", "Memanggil method class sendiri", "Memanggil constructor/method parent class", "Membuat object baru", "Menghentikan program", "B", "'super' digunakan untuk mengakses anggota (method/constructor) dari parent class."),
        SeedQuestion("inheritance", "Apa output dari?\nclass Parent { Parent() { System.out.print(\"A\"); } }\nclass Child extends Parent { Child() { System.out.print(\"B\"); } }\nnew Child();", "A", "B", "AB", "BA", "C", "Constructor parent dipanggil lebih dulu (A), lalu child (B). Output: AB."),
        SeedQuestion("inheritance", "Method overriding terjadi ketika?", "Method dengan nama sama di class yang sama", "Method di child class menggantikan method parent", "Method dipanggil berkali-kali", "Method dengan parameter berbeda", "B", "Overriding: child class mendefinisikan ulang method yang sudah ada di parent class."),
        SeedQuestion("inheritance", "Apa output dari?\nclass A { void show() { System.out.print(\"A\"); } }\nclass B extends A { void show() { System.out.print(\"B\"); } }\nA obj = new B(); obj.show();", "A", "B", "AB", "Error", "B", "Meskipun referensi A, object adalah B. Method overriding dipanggil dari B. Output: B."),
        SeedQuestion("inheritance", "Polymorphism dalam OOP berarti?", "Banyak class", "Banyak bentuk (many forms)", "Banyak method", "Banyak object", "B", "Polymorphism: kemampuan objek untuk memiliki banyak bentuk. Contoh: parent reference ke child object."),
        SeedQuestion("inheritance", "Apa output dari?\nclass A { int get() { return 1; } }\nclass B extends A { int get() { return 2; } }\nA a = new A(); System.out.print(a.get());", "1", "2", "12", "Error", "A", "a adalah object A, method get() dari A dipanggil. Output: 1."),
        SeedQuestion("inheritance", "Access modifier manakah yang dapat diakses oleh subclass di package berbeda?", "private", "default", "protected", "public", "C", "protected dan public bisa diakses dari subclass di package berbeda."),
        SeedQuestion("inheritance", "Keyword untuk mencegah inheritance suatu class adalah?", "abstract", "static", "final", "private", "C", "Class yang dideklarasikan dengan 'final' tidak bisa di-inherit."),
        SeedQuestion("inheritance", "Bisakah constructor di-inherit oleh subclass?", "Ya", "Tidak", "Hanya public", "Hanya default", "B", "Constructor tidak diwariskan. Subclass harus memanggil super() secara eksplisit atau implisit."),
        SeedQuestion("inheritance", "Apa output dari?\nclass A { A() { System.out.print(1); } }\nclass B extends A { B() { super(); System.out.print(2); } }\nnew B();", "12", "21", "1", "2", "A", "super() memanggil constructor A (cetak 1), lalu lanjut B (cetak 2). Output: 12."),
        SeedQuestion("inheritance", "Manakah yang benar tentang method private di parent class?", "Bisa di-override di child", "Tidak bisa diakses oleh child class", "Bisa diakses via super", "Otomatis jadi public", "B", "Method private hanya bisa diakses di dalam class itu sendiri, tidak oleh subclass."),
        SeedQuestion("inheritance", "Instanceof digunakan untuk?", "Membuat instance baru", "Mengecek tipe objek", "Mengubah tipe data", "Membandingkan dua objek", "B", "instanceof digunakan untuk mengecek apakah suatu objek merupakan instance dari class tertentu."),
        SeedQuestion("inheritance", "Apa output dari?\nclass Animal {}\nclass Dog extends Animal {}\nAnimal a = new Dog(); System.out.print(a instanceof Dog);", "true", "false", "Error", "null", "A", "Object a sebenarnya adalah Dog, jadi a instanceof Dog menghasilkan true."),
        SeedQuestion("inheritance", "Apa itu method overloading?", "Method sama, parameter sama", "Method sama, parameter berbeda dalam satu class", "Method di parent dan child", "Method tanpa return", "B", "Overloading: method dengan nama sama tapi parameter berbeda dalam satu class."),
        SeedQuestion("inheritance", "Apa output dari?\nclass A { void show(int a) { System.out.print(\"int\"); }\nvoid show(String a) { System.out.print(\"str\"); } }\nnew A().show(5);", "int", "str", "Error", "intstr", "A", "Parameter int 5 cocok dengan show(int a). Output: int."),
        SeedQuestion("inheritance", "Apa itu abstract class?", "Class yang tidak bisa dibuat objectnya", "Class yang bisa dibuat objectnya", "Class tanpa method", "Class dengan semua method private", "A", "Abstract class tidak bisa di-instantiate langsung. Harus di-extends dulu."),
        SeedQuestion("inheritance", "Keyword untuk mendeklarasikan method abstract adalah?", "final", "static", "abstract", "void", "C", "Method abstract dideklarasikan dengan keyword 'abstract' dan tidak memiliki body."),
        SeedQuestion("inheritance", "Interface di Java menggunakan keyword?", "class", "abstract", "interface", "implements", "C", "Interface dideklarasikan dengan keyword 'interface'."),
        SeedQuestion("inheritance", "Satu class bisa mengimplement berapa banyak interface?", "1", "2", "Tidak terbatas", "0", "C", "Java mendukung multiple interface implementation. Satu class bisa implements banyak interface."),
        SeedQuestion("inheritance", "Apa output dari?\ninterface A { default void show() { System.out.print(\"A\"); } }\nclass B implements A {}\nnew B().show();", "A", "Error", "null", "Tidak ada output", "A", "Interface dengan default method otomatis diwariskan ke class yang mengimplement. Output: A."),
        SeedQuestion("inheritance", "Bisakah abstract class memiliki constructor?", "Tidak", "Ya", "Hanya default", "Hanya private", "B", "Abstract class bisa memiliki constructor, dipanggil saat subclass di-instantiate via super()."),
        SeedQuestion("inheritance", "Manakah yang BUKAN prinsip OOP?", "Inheritance", "Encapsulation", "Polymorphism", "Compilation", "D", "4 prinsip OOP: Inheritance, Encapsulation, Polymorphism, Abstraction. Compilation bukan prinsip OOP."),

        SeedQuestion("string", "Method untuk mengetahui panjang String di Java adalah?", "length()", "size()", "count()", "getLength()", "A", "String menggunakan length() untuk mendapatkan panjang string."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.charAt(0));", "H", "e", "l", "Error", "A", "charAt(0) mengembalikan karakter di index 0 yaitu 'H'."),
        SeedQuestion("string", "Apa output dari?\nString a = \"Java\"; String b = \"Java\"; System.out.print(a.equals(b));", "true", "false", "Error", "null", "A", "equals() membandingkan nilai string. Keduanya \"Java\" → true."),
        SeedQuestion("string", "Perbedaan antara == dan equals() untuk String adalah?", "== membandingkan isi, equals membandingkan referensi", "== membandingkan referensi, equals membandingkan isi", "Tidak ada perbedaan", "equals() tidak bisa dipakai untuk String", "B", "== membandingkan referensi (alamat memori). equals() membandingkan nilai/isi string."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.substring(1,3));", "Hel", "ell", "el", "Hello", "C", "substring(1,3) ambil dari index 1 sampai sebelum 3: index 1='e', 2='l' → \"el\"."),
        SeedQuestion("string", "Apa output dari?\nString s = \" Java \"; System.out.print(s.trim().length());", "5", "6", "4", "7", "C", "trim() hapus spasi depan/belakang. \" Java \" → \"Java\" (4 karakter)."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello World\"; System.out.print(s.indexOf(\"o\"));", "4", "5", "0", "7", "A", "indexOf('o') mencari index pertama karakter 'o'. 'H'=0, 'e'=1, 'l'=2, 'l'=3, 'o'=4."),
        SeedQuestion("string", "String di Java bersifat immutable, artinya?", "String bisa diubah", "String tidak bisa diubah setelah dibuat", "String hanya untuk angka", "String tidak bisa dihapus", "B", "Immutable berarti object String tidak bisa diubah setelah dibuat."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; s.concat(\" World\"); System.out.print(s);", "Hello World", "Hello", "World", "Error", "B", "concat() mengembalikan string baru, tidak mengubah string asli (immutable). s tetap \"Hello\"."),
        SeedQuestion("string", "Method yang benar untuk mengubah String ke huruf besar adalah?", "toUpperCase()", "toUpper()", "upperCase()", "capitalize()", "A", "toUpperCase() mengubah semua huruf menjadi kapital."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.replace('l', 'x'));", "Hexxo", "Hexlo", "Hello", "Hxxlo", "A", "replace('l','x') mengganti semua 'l' dengan 'x': Hexxo."),
        SeedQuestion("string", "Apa output dari?\nString s = \"a,b,c\"; String[] arr = s.split(\",\"); System.out.print(arr.length);", "1", "2", "3", "4", "C", "split(\",\") memisah string jadi array: [\"a\",\"b\",\"c\"] → length 3."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Java\"; System.out.print(s.toUpperCase().charAt(1));", "J", "A", "V", "a", "B", "toUpperCase() → \"JAVA\", charAt(1) → 'A'."),
        SeedQuestion("string", "Apa output dari?\nString s = \"\"; System.out.print(s.isEmpty());", "true", "false", "Error", "null", "A", "isEmpty() mengembalikan true jika panjang string = 0."),
        SeedQuestion("string", "StringBuilder digunakan untuk?", "Membuat string immutable", "Memanipulasi string secara efisien tanpa membuat banyak object", "Membandingkan string", "Mengubah tipe data", "B", "StringBuilder lebih efisien untuk manipulasi string berulang karena mutable."),
        SeedQuestion("string", "Apa output dari?\nStringBuilder sb = new StringBuilder(\"Hello\"); sb.append(\" Java\"); System.out.print(sb);", "Hello", "Hello Java", "Hello Java", "Java", "B", "append() menambahkan string ke StringBuilder. Output: \"Hello Java\"."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.contains(\"ell\"));", "true", "false", "Error", "null", "A", "contains(\"ell\") mengecek apakah string mengandung substring \"ell\" → true."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.startsWith(\"He\"));", "true", "false", "Error", "null", "A", "startsWith(\"He\") mengecek apakah string diawali dengan \"He\" → true."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.endsWith(\"lo\"));", "true", "false", "Error", "null", "A", "endsWith(\"lo\") mengecek apakah string diakhiri dengan \"lo\" → true."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Java\"; System.out.print(s.indexOf(\"a\"));", "0", "1", "2", "3", "B", "'J'=0, 'a'=1. indexOf(\"a\") mencari index pertama 'a' yaitu 1."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Java\"; System.out.print(s.lastIndexOf(\"a\"));", "1", "3", "0", "2", "B", "lastIndexOf('a') mencari index terakhir 'a'. String \"Java\": J=0, a=1, v=2, a=3."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.length());", "4", "5", "6", "3", "B", "String \"Hello\" memiliki 5 karakter: H-e-l-l-o."),
        SeedQuestion("string", "Apa output dari?\nString s1 = \"Hello\"; String s2 = \"World\"; System.out.print(s1 + \" \" + s2);", "HelloWorld", "Hello World", "HelloWorld", "Error", "B", "Operator + menggabungkan string: \"Hello\" + \" \" + \"World\" = \"Hello World\"."),
        SeedQuestion("string", "Apa output dari?\nString s = \"Hello\"; System.out.print(s.compareTo(\"Hello\"));", "0", "1", "-1", "Error", "A", "compareTo() mengembalikan 0 jika kedua string sama persis."),
        SeedQuestion("string", "Apa output dari?\nString s = \"abca\"; System.out.print(s.replaceFirst(\"a\", \"x\"));", "xbca", "xbcx", "abca", "xbca", "A", "replaceFirst mengganti kemunculan pertama 'a' dengan 'x'. Output: xbca."),

        SeedQuestion("array", "Bagaimana cara mendeklarasikan array integer di Java?", "int arr[];", "int[] arr;", "array<int> arr;", "A dan B benar", "D", "int arr[] dan int[] arr sama-sama valid untuk deklarasi array di Java."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {1,2,3}; System.out.print(a[1]);", "1", "2", "3", "Error", "B", "Index array dimulai dari 0. a[0]=1, a[1]=2. Output: 2."),
        SeedQuestion("array", "Cara mendapatkan panjang array adalah?", "arr.length()", "arr.length", "arr.size()", "arr.getLength()", "B", "Array menggunakan .length (properti, bukan method) untuk mendapatkan ukuran."),
        SeedQuestion("array", "Apa output dari?\nint[] a = new int[3]; System.out.print(a[0]);", "null", "0", "1", "Error", "B", "Array int di Java diinisialisasi dengan nilai default 0 untuk setiap elemen."),
        SeedQuestion("array", "Index valid untuk array dengan length 5 adalah?", "0 sampai 4", "0 sampai 5", "1 sampai 5", "1 sampai 4", "A", "Index array dimulai dari 0 hingga length-1. length=5 → index 0-4."),
        SeedQuestion("array", "Apa yang terjadi jika mengakses index di luar batas array?", "ArrayNullException", "ArrayIndexOutOfBoundsException", "NullPointerException", "IndexException", "B", "Java melempar ArrayIndexOutOfBoundsException jika index di luar batas."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {1,2,3,4,5}; System.out.print(a[a.length-1]);", "1", "4", "5", "Error", "C", "a.length-1 = 4. a[4] = 5 (elemen terakhir). Output: 5."),
        SeedQuestion("array", "Manakah yang benar untuk iterasi seluruh elemen array?", "for(int i=0; i<=arr.length; i++)", "for(int i=0; i<arr.length; i++)", "for(int i=1; i<arr.length; i++)", "for(int i=0; i<arr.length()-1; i++)", "B", "Iterasi benar: for(int i=0; i<arr.length; i++) → mencakup semua index 0 hingga length-1."),
        SeedQuestion("array", "Array multi-dimensi 2x3 berarti memiliki?", "2 baris, 3 kolom", "3 baris, 2 kolom", "6 baris", "2 elemen", "A", "int[2][3] berarti 2 baris dan 3 kolom, total 6 elemen."),
        SeedQuestion("array", "Apa output dari?\nint[][] a = {{1,2},{3,4}}; System.out.print(a[1][0]);", "1", "2", "3", "4", "C", "a[1][0] baris index 1 (baris kedua: {3,4}), kolom index 0 → 3."),
        SeedQuestion("array", "Cara mengurutkan array di Java dengan mudah?", "arr.sort()", "Arrays.sort(arr)", "arr.order()", "Collection.sort(arr)", "B", "Arrays.sort(arr) dari package java.util.Arrays untuk mengurutkan array."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {3,1,2}; java.util.Arrays.sort(a); System.out.print(a[0]);", "1", "2", "3", "0", "A", "Setelah sort, array menjadi {1,2,3}. a[0] = 1."),
        SeedQuestion("array", "Cara mencetak isi array dengan benar?", "System.out.print(arr)", "System.out.print(Arrays.toString(arr))", "System.out.print(arr.toString())", "System.out.print(arr.print())", "B", "Arrays.toString(arr) mengembalikan representasi string dari array seperti [1, 2, 3]."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {1,2,3}; int[] b = a; b[0] = 99; System.out.print(a[0]);", "1", "99", "0", "Error", "B", "b = a berarti b mereferensi ke array yang sama. Mengubah b[0] juga mengubah a[0]."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {10,20,30}; int sum = 0;\nfor(int n : a) sum += n; System.out.print(sum);", "50", "60", "70", "30", "B", "sum = 10+20+30 = 60. For-each loop menjumlahkan semua elemen."),
        SeedQuestion("array", "Apa nilai default elemen array boolean?", "true", "false", "null", "0", "B", "Array boolean diinisialisasi dengan nilai default false."),
        SeedQuestion("array", "Apa output dari?\nint[] a = new int[5]; System.out.print(a.length);", "4", "5", "6", "0", "B", "new int[5] membuat array dengan 5 elemen. a.length = 5."),
        SeedQuestion("array", "Bagaimana cara menyalin array di Java?", "arr.copy()", "System.arraycopy() atau Arrays.copyOf()", "arr.clone()", "B dan C benar", "D", "System.arraycopy(), Arrays.copyOf(), dan clone() semua bisa digunakan untuk menyalin array."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {1,2,3,4,5}; System.out.print(a[2]);", "1", "2", "3", "4", "C", "Index 2 adalah elemen ke-3: 3."),
        SeedQuestion("array", "Array di Java bersifat?", "Mutable ukurannya", "Fixed size", "Dinamis", "Tidak terbatas", "B", "Setelah dibuat, ukuran array di Java tidak bisa diubah (fixed size)."),
        SeedQuestion("array", "Apa output dari?\nString[] s = {\"a\",\"b\",\"c\"}; System.out.print(s[1].toUpperCase());", "A", "B", "C", "a", "B", "s[1] = \"b\", toUpperCase() → \"B\"."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {1,2,3,4,5};\nint max = a[0]; for(int n : a) if(n>max) max=n; System.out.print(max);", "1", "3", "5", "4", "C", "Mencari nilai maksimum dalam array: 5."),
        SeedQuestion("array", "Array 2D int[3][4] memiliki total elemen?", "7", "12", "3", "4", "B", "3 baris × 4 kolom = 12 elemen."),
        SeedQuestion("array", "Apa output dari?\nint[] a = {1,2,3};\nSystem.out.print(java.util.Arrays.binarySearch(a, 2));", "0", "1", "2", "-1", "B", "binarySearch mencari nilai 2. Array {1,2,3}, index 2 adalah 1."),
        SeedQuestion("array", "Manakah deklarasi array 2D yang benar?", "int[][] a = new int[2][3];", "int a[][] = new int[2][3];", "int[] a[] = new int[2][3];", "Semua benar", "D", "Ketiga cara tersebut valid untuk deklarasi array 2D di Java."),
    )

    private data class SeedQuestion(
        val categoryId: String,
        val questionText: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val optionD: String,
        val correctAnswer: String,
        val explanation: String
    )

    suspend fun seedQuestions(clearExisting: Boolean = false): Int {
        val databases = AppwriteClient.get().databases
        val dbId = AppwriteClient.DATABASE_ID
        val collId = AppwriteClient.QUESTIONS_COLLECTION_ID

        return withContext(Dispatchers.IO) {
            if (clearExisting) {
                try {
                    val existing = databases.listDocuments(dbId, collId, listOf(io.appwrite.Query.limit(100)))
                    existing.documents.forEach { doc ->
                        try { databases.deleteDocument(dbId, collId, doc.id) } catch (_: Exception) {}
                    }
                } catch (_: Exception) {}
            }

            var count = 0
            for (q in questionsData) {
                try {
                    databases.createDocument(
                        databaseId = dbId,
                        collectionId = collId,
                        documentId = ID.unique(),
                        data = mapOf(
                            "category_id" to q.categoryId,
                            "question_text" to q.questionText,
                            "option_a" to q.optionA,
                            "option_b" to q.optionB,
                            "option_c" to q.optionC,
                            "option_d" to q.optionD,
                            "correct_answer" to q.correctAnswer,
                            "explanation" to q.explanation
                        )
                    )
                    count++
                } catch (_: Exception) {}
            }
            count
        }
    }
}
