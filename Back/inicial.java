class Program {
    String name;
    list <Asignatura> pensum;
    double price;
}

class Asignature{
    String name;
    int semester;
}

class educationalPackage{
    Program program;
    double discount;
}

class User{
    String name;
    String email;
    int age;
    int telephone;
    List<educationalPackage> packages;
}