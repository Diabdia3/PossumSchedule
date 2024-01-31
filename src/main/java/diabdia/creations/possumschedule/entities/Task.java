package diabdia.creations.possumschedule.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    @Column(length = 2000)
    private String content;
    private int priority;
    private boolean completed;
    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public Task() {}

    public Task(String name, String content, int priority, boolean completed){
        this.name = name;
        this.content = content;
        this.priority = priority;
        this.completed = completed;
    }

    @Override
    public String toString(){
        return name + "\n" + content + "\n" + priority + "\n" + completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        if (priority != task.priority) return false;
        if (completed != task.completed) return false;
        if (!id.equals(task.id)) return false;
        if (!name.equals(task.name)) return false;
        return !content.equals(task.content);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + priority;
        result = 31 * result + (completed ? 1 : 0);
        return result;
    }
}
