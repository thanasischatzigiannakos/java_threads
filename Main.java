import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main extends Thread {
    public static void main(String[] args) throws InterruptedException {

        /**
         * First part of the code implements the desired functionality using a single
         * thread
         * Later we use Threads to split the same functionality into 4 different threads
         * ( as many urls / requests we have)
         **/

        String urls[] = {
                "https://www.imdb.com/search/title/?title_type=tv_series&release_date=2020-01-01,2021-12-31&countries=us",
                "https://www.imdb.com/search/title/?title_type=tv_series&release_date=2019-01-01,2019-12-31",
                "https://www.imdb.com/search/title/?title_type=tv_series&year=2018-01-01,2018-12-31",
                "https://www.imdb.com/search/title/?title_type=tv_series&year=2017,2017&sort=moviemeter,a" };

        String seriesDate[] = { "2020-2021", "2019", "2018", "2017" };
        HttpClient client = HttpClient.newHttpClient(); // Create the Client in order to make a request to the desired
                                                        // url
        long startTime = System.nanoTime();
        for (int x = 0; x < urls.length; x++) { // loop through the urls in order to execute the code for each URL
                                                // provided
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urls[x]))
                    .build();
            try {
                HttpResponse<String> response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                String processedCode = response.body().replaceAll("[^a-zA-Z]+", " "); // try to clear the HTML code in
                                                                                      // order to find the words more
                                                                                      // easily
                String splitResponseString[] = processedCode.split(" "); // after the code has been cleaned we split the
                                                                         // string in order to get standalone words

                int dramaCount = 0;
                int actionCount = 0;
                int comedyCount = 0;
                for (int i = 0; i < splitResponseString.length; i++) { // for each occurence of a desired word we
                                                                       // increment the desired counter value
                    if (splitResponseString[i].equals("Drama")) {
                        dramaCount++;
                    }
                    if (splitResponseString[i].equals("Action")) {
                        actionCount++;
                    }
                    if (splitResponseString[i].equals("Comedy")) {
                        comedyCount++;
                    }
                }

                System.out.println("Total occurances of Drama for date " + seriesDate[x] + " are :" + dramaCount); // print
                                                                                                                   // the
                                                                                                                   // results
                System.out.println("Total occurances of Action for date " + seriesDate[x] + " are :" + actionCount);
                System.out.println("Total occurances of Comedy for date " + seriesDate[x] + " are :" + comedyCount);
                System.out.println("URL called : " + urls[x]);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Time elapsed using single main thread: " + estimatedTime);

        startTime = System.nanoTime();

        /**
         * From this part and on we create the Thread functionality
         **/

        MultithreadImplementation thread1 = new MultithreadImplementation("Thread-1",
                "https://www.imdb.com/search/title/?title_type=tv_series&release_date=2020-01-01,2021-12-31&countries=us",
                "2020");
        MultithreadImplementation thread2 = new MultithreadImplementation("Thread-2",
                "https://www.imdb.com/search/title/?title_type=tv_series&release_date=2019-01-01,2019-12-31", "2019");
        MultithreadImplementation thread3 = new MultithreadImplementation("Thread-3",
                "https://www.imdb.com/search/title/?title_type=tv_series&year=2018-01-01,2018-12-31", "2018");
        MultithreadImplementation thread4 = new MultithreadImplementation("Thread-4",
                "https://www.imdb.com/search/title/?title_type=tv_series&year=2017,2017&sort=moviemeter,a", "2017");

        MultithreadImplementation threadArray[] = { thread1, thread2, thread3, thread4 }; // we create this array in
                                                                                          // order to use the join
                                                                                          // function to see when all of
                                                                                          // the threads have run
                                                                                          // successfully
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        for (int i = 0; i < threadArray.length; i++)
            threadArray[i].join();

        estimatedTime = System.nanoTime() - startTime;
        System.out.println("Time elapsed using multiple threads: " + estimatedTime);
    }
}

class MultithreadImplementation extends Thread implements Runnable {
    private Thread t;
    private String threadName;
    private String url;
    private String seriesDate;

    MultithreadImplementation(String threadName, String url, String seriesDate) {
        this.threadName = threadName;
        this.url = url;
        this.seriesDate = seriesDate;
        System.out.println("Thread " + threadName + " created!");
    }

    @Override
    public void run() {
        System.out.println("Running : " + threadName);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String processedCode = response.body().replaceAll("[^a-zA-Z]+", " ");
            String splitResponseString[] = processedCode.split(" ");

            int dramaCount = 0;
            int actionCount = 0;
            int comedyCount = 0;
            for (int i = 0; i < splitResponseString.length; i++) {
                if (splitResponseString[i].equals("Drama")) {
                    dramaCount++;
                }
                if (splitResponseString[i].equals("Action")) {
                    actionCount++;
                }
                if (splitResponseString[i].equals("Comedy")) {
                    comedyCount++;
                }
            }

            System.out.println("Total occurances of Drama for date " + seriesDate + " are :" + dramaCount);
            System.out.println("Total occurances of Action for date " + seriesDate + " are :" + actionCount);
            System.out.println("Total occurances of Comedy for date " + seriesDate + " are :" + comedyCount);
            System.out.println("URL called : " + url);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

}