package gui.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import application.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import model.Schedule;

public class Notifier {

	// Mapeamento de dias da semana em português para DayOfWeek
	private static final Map<String, DayOfWeek> DIAS_SEMANA = new HashMap<>();

	// Conjunto para armazenar tarefas que já tiveram o Alert exibido
	private static final Set<Schedule> notifiedSchedules = new HashSet<>();

	static {
		DIAS_SEMANA.put("segunda", DayOfWeek.MONDAY);
		DIAS_SEMANA.put("terca", DayOfWeek.TUESDAY);
		DIAS_SEMANA.put("quarta", DayOfWeek.WEDNESDAY);
		DIAS_SEMANA.put("quinta", DayOfWeek.THURSDAY);
		DIAS_SEMANA.put("sexta", DayOfWeek.FRIDAY);
		DIAS_SEMANA.put("sabado", DayOfWeek.SATURDAY);
		DIAS_SEMANA.put("domingo", DayOfWeek.SUNDAY);
	}

	/**
	 * Inicia o notificador para verificar tarefas próximas.
	 *
	 * @param schedules Lista de cronogramas (schedules) a serem verificados.
	 */
	public static void startNotifier(List<Schedule> schedules) {
		if (schedules == null || schedules.isEmpty()) {
			System.out.println("Nenhum cronograma para verificar.");
			return;
		}

		ScheduledExecutorService executor = Main.getExecutor();

		// Adiciona um hook para encerrar o executor ao fechar a aplicação
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Encerrando o Notifier...");
			executor.shutdown(); // Encerra o executor
		}));

		System.out.println("Notifier iniciado. Verificando tarefas a cada 20 minutos.");

		executor.scheduleAtFixedRate(() -> {
			LocalDateTime now = LocalDateTime.now();
			System.out.println("Verificando tarefas em: " + now);

			for (Schedule sche : schedules) {
				try {
					// Converte o dia da semana e a hora para LocalDateTime
					LocalDateTime dateTime = convertToDateTime(sche.getDayWeek(), sche.getHour());
					System.out.println("Cronograma: " + sche.getSubject() + " - " + sche.getTalkAbout()
							+ " | Data/Hora: " + dateTime);

					// Verifica se a tarefa está próxima (dentro de 60 minutos)
					if (dateTime.isAfter(now) && dateTime.isBefore(now.plusMinutes(60))) {
						// Verifica se o Alert já foi exibido para essa tarefa
						if (!notifiedSchedules.contains(sche)) {
							if (Platform.isFxApplicationThread()) {
								// Se já estiver na thread do JavaFX, exibe o Alert diretamente
								System.out.println(
										"Exibindo Alert para: " + sche.getSubject() + " - " + sche.getTalkAbout());
								Alerts.showAlert("Lembrete", "Tarefa Próxima",
										sche.getSubject() + " - " + sche.getTalkAbout(), AlertType.INFORMATION);
							} else {
								// Caso contrário, usa Platform.runLater
								Platform.runLater(() -> {
									System.out.println(
											"Exibindo Alert para: " + sche.getSubject() + " - " + sche.getTalkAbout());
									Alerts.showAlert("Lembrete", "Tarefa Próxima",
											sche.getSubject() + " - " + sche.getTalkAbout(), AlertType.INFORMATION);
								});
							}
							// Marca a tarefa como notificada
							notifiedSchedules.add(sche);
						}
					}
				} catch (Exception e) {
					System.err.println(
							"Erro ao processar cronograma: " + sche.getSubject() + " - " + sche.getTalkAbout());
					System.err.println("Detalhes do erro: " + e.getMessage());
				}
			}
		}, 0, 20, TimeUnit.MINUTES); // Verifica a cada 20 minutos
	}

	/**
	 * Converte o dia da semana e a hora em um objeto LocalDateTime.
	 *
	 * @param dayWeek Dia da semana em português (ex: "segunda", "terca").
	 * @param hour    Hora no formato "HH:mm".
	 * @return Um objeto LocalDateTime combinando a próxima ocorrência do dia da
	 *         semana com a hora.
	 * @throws IllegalArgumentException Se o dia da semana for inválido.
	 */
	public static LocalDateTime convertToDateTime(String dayWeek, String hour) {
		System.out.println("Convertendo: Dia = " + dayWeek + " | Hora = " + hour);

		if (!DIAS_SEMANA.containsKey(dayWeek.toLowerCase())) {
			throw new IllegalArgumentException("Dia da semana inválido: " + dayWeek);
		}

		LocalTime time = LocalTime.parse(hour, DateTimeFormatter.ofPattern("HH:mm"));
		DayOfWeek dayOfWeek = DIAS_SEMANA.get(dayWeek.toLowerCase());

		if (dayOfWeek == null) {
			throw new IllegalArgumentException("Dia da semana inválido: " + dayWeek);
		}

		// Obtém a próxima ocorrência do dia da semana
		LocalDate nextDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayOfWeek));
		LocalDateTime dateTime = LocalDateTime.of(nextDate, time);

		System.out.println("Data/Hora convertida: " + dateTime);
		return dateTime;
	}
}