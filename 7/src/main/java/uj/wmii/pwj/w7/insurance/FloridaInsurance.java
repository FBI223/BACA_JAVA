package uj.wmii.pwj.w7.insurance;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FloridaInsurance {

    public static void main(String[] args) {

        Path currentDir = Paths.get(".").toAbsolutePath().normalize();

        Path zipFilePath = currentDir.resolve("FL_insurance.csv.zip");

        try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
            ZipEntry floridaEntry = zipFile.getEntry("FL_insurance.csv");
            if ( floridaEntry != null )
            {
                try (InputStream inputStream = zipFile.getInputStream(floridaEntry);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {


                    List<InsuranceEntry> records = reader.lines()
                            .skip(1)
                            .map(line -> {
                                String[] data = line.split(",");
                                return new InsuranceEntry(
                                        data[0],  // policyId
                                        data[1],  // state
                                        data[2],  // county
                                        data[3],  // eqSiteLimit
                                        data[4],  // huSiteLimit
                                        data[5],  // flSiteLimit
                                        data[6],  // frSiteLimit
                                        data[7],  // tiv2011
                                        data[8],  // tiv2012
                                        data[9],  // eqSiteDeductible
                                        data[10], // huSiteDeductible
                                        data[11], // flSiteDeductible
                                        data[12], // frSiteDeductible
                                        data[13], // pointLatitude
                                        data[14], // pointLongitude
                                        data[15], // line
                                        data[16], // construction
                                        data[17]  // pointGrading
                                );
                            }).toList();


                    //1
                    Long ilosc_krain =  records.stream().map( record -> record.getCounty() ).distinct().count();
                    String ilosc_krain_string = String.format("%d" ,ilosc_krain);

                    //2
                    Double suma_tiv_2012 = records.stream().map(record -> record.getTiv2012()).reduce((double) 0,Double::sum);
                    String suma_tiv_string = String.format( Locale.US, "%.2f", suma_tiv_2012);

                    //3
                    List<Map.Entry<String, Double>> top10 = records.stream()
                            .collect(Collectors.groupingBy(
                                    record -> record.getCounty(), // Grupowanie po hrabstwie
                                    Collectors.summingDouble(record -> record.getTiv2012() - record.getTiv2011())
                            )).entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10).toList();



                    Path countPath = currentDir.resolve("count.txt");
                    Path mostValPath = currentDir.resolve("most_valuable.txt");
                    Path tiv2012Path = currentDir.resolve("tiv2012.txt");

                    Files.deleteIfExists(countPath);
                    Files.deleteIfExists(tiv2012Path);
                    Files.deleteIfExists(mostValPath);

                    Files.write(countPath,ilosc_krain_string.getBytes());
                    Files.write(tiv2012Path,suma_tiv_string.getBytes());


                    try (BufferedWriter writer = Files.newBufferedWriter(mostValPath);){
                        writer.write("country,value");
                        for (Map.Entry<String, Double> entry : top10 ) {
                            writer.write( String.format(Locale.US, "\n%s,%.2f", entry.getKey(), entry.getValue() ));
                        }
                    }
                }
            }

            } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
