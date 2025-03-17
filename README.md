# Agenda java 21
---
## Libs
- javafx-sdk-23.0.2
- SQLite jdbc-3.49.1.0
---
### Testing the Notifier class
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


### Login View - Register
![Capturar1](https://github.com/user-attachments/assets/76d1e0ec-f863-4b40-abff-cb976805d7c3)
![Capturar2](https://github.com/user-attachments/assets/bf5f3056-0469-4cd1-8f16-54d970f09c3d)

---
### Main View - Summary View - KeyWord View

![Capturar3](https://github.com/user-attachments/assets/63a6dc9b-e124-47f9-aad4-4f9201409b29)
![Capturar4](https://github.com/user-attachments/assets/04d4eab9-f8cb-44db-98de-a8a6a4075fa7)
---
### Schedule
![Capturar5](https://github.com/user-attachments/assets/5110d3bb-ab45-4e61-aae3-420d67feef7b)
![Capturar6](https://github.com/user-attachments/assets/787f3797-ff92-418d-8698-9875f0d7f236)

### Search - Popup
![Capturar7](https://github.com/user-attachments/assets/7129174c-2c73-4395-b353-f73a15328695)
                
                        
           private void searchSummary(String termo) {
          		if (mainApp == null) {
                       		System.err.println("Erro: mainApp não foi inicializado!");
                		return;
                	}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();
		List<Summary> result = summaryDAO.searchByWord(termo, userId);

		if (result.isEmpty()) {
			msgFeedback.setText("Nenhum resumo encontrado para: " + termo);
		} else {
			listResult.getItems().clear();
			listResult.getItems().addAll(result);
			msgFeedback.setText(result.size() + " resumo(s) encontrado(s)" + termo);
		}
	}

	private void searchKeyWord(String termo) {
		if (mainApp == null) {
			System.err.println("Erro: mainApp não foi inicializado!");
			return;
		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();
		List<KeyWord> result = keyWordDAO.searchByTerm(termo, userId);

		if (result.isEmpty()) {
			msgFeedback.setText("Nenhuma palavra-chave encontrada para: " + termo);
		} else {
			listResult.getItems().clear(); //
			listResult.getItems().addAll(result);
			msgFeedback.setText(result.size() + " palavra(s)-chave encontrada(s)" + termo);
		}
	}

         @FXML
        	public void listAll() {
        		if (mainApp == null) {
        			System.err.println("Erro: mainApp não foi inicializado!");
        			return;
        		}

		if (mainApp.getLoggedUser() == null) {
			System.err.println("Erro: Usuário logado não foi definido!");
			return;
		}
		userId = mainApp.getLoggedUser().getId();

		listResult.getItems().clear();

		List<Summary> summaries = summaryDAO.findAllByUserId(userId);
		if (!summaries.isEmpty()) {
			listResult.getItems().addAll(summaries);
		}

		List<KeyWord> keyWords = keyWordDAO.findAllByUserId(userId);
		if (!keyWords.isEmpty()) {
			listResult.getItems().addAll(keyWords);
		}

		// Exibe feedback ao usuário
		if (listResult.getItems().isEmpty()) {
			msgFeedback.setText("Nenhum resumo ou palavra-chave encontrado.");
		} else {
			msgFeedback.setText("Listando todos os resumos e palavras-chave.");
		}
	}


![Capturar8](https://github.com/user-attachments/assets/34acf1ce-c679-4269-a5f9-3822a575566f)

