import java.util.HashMap;
import java.util.Map;

public class CustomThing {
    public static void main(String[] args) {
        String m="01 : [Ali Lahouar - Ens. II];Walid Chainbi - Ens. Perm MA II;Aref Meddeb - Ens. Perm P II\t02 : [Anis Ben Arbia - Ens. Perm MA II];Imed Bennour - Ens. Perm MA II;Lotfi Hamrouni - Ens. Perm A II\n" +
                "03 : [Anis Ben Arbia - Ens. Perm MA II];Naoufel Khayati - Ens. Perm MA II;Walid Chainbi - Ens. Perm MA II\t08 : [Aref Meddeb - Ens. Perm P II];Imen Khadhraoui - Ens. V II;Taha Ben Salah - Ens. Perm MA II\n" +
                "05 : [Anis Ben Arbia - Ens. Perm MA II];Saoussen Ben Jabra - Ens. Perm MA II;Walid Chainbi - Ens. Perm MA II\t09 : [Aref Meddeb - Ens. Perm P II];Jamel Bel Hadj Taher - Ens. Perm P II;Saoussen Ben Jabra - Ens. Perm MA II\n" +
                "06 : [Anis Ben Arbia - Ens. Perm MA II];Manel Abdel Hedi - Ens. Perm A II;Walid Chainbi - Ens. Perm MA II\t16 : [Jamel Bel Hadj Taher - Ens. Perm P II];Naoufel Khayati - Ens. Perm MA II;Aref Meddeb - Ens. Perm P II\n" +
                "07 : [Aref Meddeb - Ens. Perm P II];Mohamed Nazih Omri - Ens. Perm P II;Walid Chainbi - Ens. Perm MA II\t15 : [Jamel Bel Hadj Taher - Ens. Perm P II];Manel Abdel Hedi - Ens. Perm A II;Naoufel Khayati - Ens. Perm MA II\n" +
                "24 : [Mohamed Nazih Omri - Ens. Perm P II];Walid Chainbi - Ens. Perm MA II;Jamel Bel Hadj Taher - Ens. Perm P II\t12 : [Imed Bennour - Ens. Perm MA II];Aref Meddeb - Ens. Perm P II;Mohamed Nazih Omri - Ens. Perm P II\n" +
                "30 : [Saoussen Ben Jabra - Ens. Perm MA II];Taha Ben Salah - Ens. Perm MA II;Walid Chainbi - Ens. Perm MA II\t13 : [Imen Khadhraoui - Ens. V II];Jamel Bel Hadj Taher - Ens. Perm P II;Manel Abdel Hedi - Ens. Perm A II\n" +
                "31 : [Saoussen Ben Jabra - Ens. Perm MA II];Walid Chainbi - Ens. Perm MA II;Mohamed Nazih Omri - Ens. Perm P II\t25 : [Mohamed Nazih Omri - Ens. Perm P II];Taha Ben Salah - Ens. Perm MA II;Anis Ben Arbia - Ens. Perm MA II\n" +
                "32 : [Saoussen Ben Jabra - Ens. Perm MA II];Jamel Bel Hadj Taher - Ens. Perm P II;Walid Chainbi - Ens. Perm MA II\t22 : [Manel Abdel Hedi - Ens. Perm A II];Anis Ben Arbia - Ens. Perm MA II;Aref Meddeb - Ens. Perm P II\n" +
                "36 : [Walid Chainbi - Ens. Perm MA II];Aref Meddeb - Ens. Perm P II;Anis Ben Arbia - Ens. Perm MA II\t29 : [Saoussen Ben Jabra - Ens. Perm MA II];Taha Ben Salah - Ens. Perm MA II;Imen Khadhraoui - Ens. V II\n" +
                "37 : [Walid Chainbi - Ens. Perm MA II];Anis Ben Arbia - Ens. Perm MA II;Mohamed Nazih Omri - Ens. Perm P II\t19 : [Lotfi Hamrouni - Ens. Perm A II];Saoussen Ben Jabra - Ens. Perm MA II;Taha Ben Salah - Ens. Perm MA II\n" +
                "38 : [Walid Chainbi - Ens. Perm MA II];Saoussen Ben Jabra - Ens. Perm MA II;Jamel Bel Hadj Taher - Ens. Perm P II\t23 : [Mohamed Nazih Omri - Ens. Perm P II];Anis Ben Arbia - Ens. Perm MA II;Manel Abdel Hedi - Ens. Perm A II\n" +
                "39 : [Walid Chainbi - Ens. Perm MA II];Anis Ben Arbia - Ens. Perm MA II;Manel Abdel Hedi - Ens. Perm A II\t33 : [Taha Ben Salah - Ens. Perm MA II];Ali Lahouar - Ens. II;Imed Bennour - Ens. Perm MA II\n" +
                "40 : [Walid Chainbi - Ens. Perm MA II];Aref Meddeb - Ens. Perm P II;Anis Ben Arbia - Ens. Perm MA II\t34 : [Taha Ben Salah - Ens. Perm MA II];Lotfi Hamrouni - Ens. Perm A II;Naoufel Khayati - Ens. Perm MA II\n" +
                "41 : [Walid Chainbi - Ens. Perm MA II];Saoussen Ben Jabra - Ens. Perm MA II;Jamel Bel Hadj Taher - Ens. Perm P II\t\n" +
                "42 : [Walid Chainbi - Ens. Perm MA II];Aref Meddeb - Ens. Perm P II;Ali Lahouar - Ens. II\t28 : [Naoufel Khayati - Ens. Perm MA II];Mohamed Nazih Omri - Ens. Perm P II;Saoussen Ben Jabra - Ens. Perm MA II\n" +
                "43 : [Walid Chainbi - Ens. Perm MA II];Jamel Bel Hadj Taher - Ens. Perm P II;Naoufel Khayati - Ens. Perm MA II\t\n" +
                "11 : [Imed Bennour - Ens. Perm MA II];Jamel Bel Hadj Taher - Ens. Perm P II;Walid Chainbi - Ens. Perm MA II\t\n" +
                "10 : [Aref Meddeb - Ens. Perm P II];Walid Chainbi - Ens. Perm MA II;Jamel Bel Hadj Taher - Ens. Perm P II\t17 : [Jamel Bel Hadj Taher - Ens. Perm P II];Imed Bennour - Ens. Perm MA II;Anis Ben Arbia - Ens. Perm MA II\n" +
                "21 : [Manel Abdel Hedi - Ens. Perm A II];Walid Chainbi - Ens. Perm MA II;Saoussen Ben Jabra - Ens. Perm MA II\t04 : [Anis Ben Arbia - Ens. Perm MA II];Naoufel Khayati - Ens. Perm MA II;Taha Ben Salah - Ens. Perm MA II\n" +
                "20 : [Manel Abdel Hedi - Ens. Perm A II];Walid Chainbi - Ens. Perm MA II;Aref Meddeb - Ens. Perm P II\t26 : [Naoufel Khayati - Ens. Perm MA II];Mohamed Nazih Omri - Ens. Perm P II;Jamel Bel Hadj Taher - Ens. Perm P II\n" +
                "27 : [Naoufel Khayati - Ens. Perm MA II];Walid Chainbi - Ens. Perm MA II;Saoussen Ben Jabra - Ens. Perm MA II\t18 : [Jamel Bel Hadj Taher - Ens. Perm P II];Anis Ben Arbia - Ens. Perm MA II;Imed Bennour - Ens. Perm MA II\n" +
                "35 : [Taha Ben Salah - Ens. Perm MA II];Manel Abdel Hedi - Ens. Perm A II;Walid Chainbi - Ens. Perm MA II\t\n" +
                "14 : [Jamel Bel Hadj Taher - Ens. Perm P II];Walid Chainbi - Ens. Perm MA II;Anis Ben Arbia - Ens. Perm MA II\t\n";
        Map<String,Integer> nameId=new HashMap<>();
        for (String line : m.split("\n")) {
            for (String col : line.split("\t")) {
                String[] idAndPersons = col.split(":");
                System.out.print(idAndPersons[0].trim()+";");
                for (String name : idAndPersons[1].split(";")) {
                    name=name.trim();
                    if(name.startsWith("[")){
                        name=name.substring(1,name.length()-1);
                    }
                    Integer found = nameId.get(name);
                    if(found==null){
                        found=nameId.size()+1;
                        nameId.put(name,found);
                    }
                    System.out.print(found+";");
                    System.out.print(name.split("-")[0].trim()+";");
                }
            }
            System.out.println("");
        }
    }
}
