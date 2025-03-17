package application;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import gui.util.Notifier;
import model.Schedule;

public class NotifierTest {

    public static void main(String[] args) {
        // Cria uma lista de cronogramas de teste
        List<Schedule> schedules = new ArrayList<>();

        // Adiciona cronogramas com horários próximos ao momento atual
        schedules.add(new Schedule("segunda", LocalTime.now().plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm")), "Matemática", "Álgebra Linear", 1));
        schedules.add(new Schedule("terca", LocalTime.now().plusMinutes(20).format(DateTimeFormatter.ofPattern("HH:mm")), "Física", "Termodinâmica", 1));

        // Inicia o notificador
        Notifier.startNotifier(schedules);

        // Mantém o programa em execução para testar as notificações
        try {
            Thread.sleep(60000); // Aguarda 1 minuto
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}