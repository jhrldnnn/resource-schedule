package com.thirdnf.ResourceScheduler.demo;

import com.thirdnf.ResourceScheduler.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.experimental.categories.Categories;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A demo model, has its database hard coded and it is only capable of showing two days, Today and
 * Tomorrow.  Everything else is blank and will ignore updates.
 *
 * @author Joshua Gerth - jgerth@thirdnf.com
 */
public class ScheduleModelDemo extends AbstractScheduleModel
{
    public static final LocalDate Today    = new LocalDate();
    public static final LocalDate Tomorrow = Today.plusDays(1);

    private static final List<Resource> TodayResources    = new ArrayList<Resource>();
    private static final List<Resource> TomorrowResources = new ArrayList<Resource>();

    private static final List<Appointment> TodayAppointments = new ArrayList<Appointment>();
    private static final List<Appointment> TomorrowAppointments = new ArrayList<Appointment>();

    // Resources
    private static final DemoResource Bobby = new DemoResource("Bobby", new Color(251, 198, 12, 200));
    private static final DemoResource Johnny = new DemoResource("Johnny", new Color(12, 251, 160, 200));
    private static final DemoResource Sally = new DemoResource("Sally", new Color(166, 251, 12, 200));
    private static final DemoResource Freddy = new DemoResource("Freddy", new Color(66, 151, 12, 200));

    // Our categories for our appointments
    private static final DemoCategory Green = new DemoCategory("Green", new Color(9, 246, 76, 200));
    private static final DemoCategory Blue  = new DemoCategory("Blue", new Color(9, 171, 246, 200));

    // This initializes the defaults.
    static {
        TodayResources.add(Bobby);
        TodayResources.add(Johnny);
        TodayResources.add(Sally);

        // Freddy is not going to be listed for today, but an appointment today is going to be assigned
        //  to Freddy.  The appointment should show up in the first column for the day.

        // Populate some default appointments
        TodayAppointments.add(DemoAppointment.create("Appointment1", Green, Bobby, new LocalTime(10, 5, 0),  45));
        TodayAppointments.add(DemoAppointment.create("Appointment2", Blue, Johnny, new LocalTime(13, 0, 0), 75));
        TodayAppointments.add(DemoAppointment.create("Appointment3", Blue, Sally, new LocalTime(8, 0, 0), 60));
        TodayAppointments.add(DemoAppointment.create("Appointment4", Green, Sally, new LocalTime(8, 45, 0), 120));
        TodayAppointments.add(DemoAppointment.create("Appointment5", Blue, Sally, new LocalTime(10, 45, 0), 30));
        TodayAppointments.add(DemoAppointment.create("Appointment7", Green, Sally, new LocalTime(12, 30, 0), 40));
        TodayAppointments.add(DemoAppointment.create("Appointment8", Blue, Freddy, new LocalTime(13, 0, 0), 50));
    }


    /**
     * Boring empty constructor for our model.
     */
    public ScheduleModelDemo()
    {
    }


    @Override
    public void visitAppointments(AppointmentVisitor visitor, @NotNull LocalDate dateTime)
    {
        List<Appointment> appointments;
        if (dateTime.equals(Today)) {
            appointments = TodayAppointments;
        }
        else if (dateTime.equals(Tomorrow)) {
            appointments = TomorrowAppointments;
        }
        else {
            return;
        }

        for (Appointment appointment : appointments) {
            visitor.visitAppointment(appointment);
        }
    }


    @Override
    public void visitResources(ResourceVisitor visitor, @NotNull LocalDate date)
    {
        List<Resource> resources;
        if (date.equals(Today)) {
            resources = TodayResources;
        }
        else if (date.equals(Tomorrow)) {
            resources = TomorrowResources;
        }
        else {
            return;
        }

        for (Resource resource : resources) {
            visitor.visitResource(resource);
        }
    }


    public void visitCategories(@NotNull CategoryVisitor visitor)
    {
        visitor.visitCategory(Green);
        visitor.visitCategory(Blue);
    }


    /**
     * Our model has been told to add a resource to its database.  This method will add the
     *  resource to the underlying database and then trigger a redraw to any components using
     *  this model.
     *
     * @param resource (not null) Resource to add
     * @param date (not null) The date to add the resource to.
     * @param index Position to add the resource, -1 indicates that it should be added a the end.
     */
    public void addResource(@NotNull Resource resource, LocalDate date, int index)
    {
        List<Resource> resources;
        if (date.equals(Today)) {
            resources = TodayResources;
        }
        else if (date.equals(Tomorrow)) {
            resources = TomorrowResources;
        }
        else {
            return;
        }

        resources.add(resource);

        fireResourceAdded(resource, date, index);
    }


    public void updateResource(@NotNull Resource resource)
    {
        fireResourceUpdated(resource);
    }


    public void deleteResource(@NotNull Resource resource, @NotNull LocalDate date)
    {
        List<Resource> resources;
        if (date.equals(Today)) {
            resources = TodayResources;
        }
        else if (date.equals(Tomorrow)) {
            resources = TomorrowResources;
        }
        else {
            return;
        }

        resources.remove(resource);
        fireResourceRemoved(resource, date);
    }


    public void deleteAppointment(@NotNull Appointment appointment)
    {
        LocalDate date = appointment.getDateTime().toLocalDate();

        List<Appointment> appointments;
        if (date.equals(Today)) {
            appointments = TodayAppointments;
        }
        else if (date.equals(Tomorrow)) {
            appointments = TomorrowAppointments;
        }
        else {
            return;
        }

        // Remove it from our list
        appointments.remove(appointment);

        // Let any listeners know we have removed this appointment.
        fireAppointmentRemoved(appointment);
    }


    @Override
    public LocalTime getEndTime(@NotNull LocalDate dateTime)
    {
        return new LocalTime(18, 0, 0); // 6 pm
    }


    @Override
    public LocalTime getStartTime(@NotNull LocalDate dateTime)
    {
        return new LocalTime(8, 0, 0); // 8 am
    }


    public static class DemoAppointment implements Appointment
    {
        private final DemoCategory _category;
        private final Resource _resource;
        private final String _title;
        private DateTime _time;
        private Duration _length;


        public DemoAppointment(@NotNull String title, DemoCategory category, Resource resource)
        {
            _title = title;
            _category = category;
            _resource = resource;
        }


        public DemoCategory getCategory()
        {
            return _category;
        }

        @NotNull
        @Override
        public DateTime getDateTime()
        {
            return _time;
        }


        @Override
        public Resource getResource()
        {
            return _resource;
        }

        @NotNull
        @Override
        public Duration getDuration()
        {
            return _length;
        }

        @NotNull
        @Override
        public String getTitle()
        {
            return _title;
        }


        public void setTime(@NotNull DateTime time)
        {
            _time = time;
        }


        public void setLength(@NotNull Duration length)
        {
            _length = length;
        }


        public static DemoAppointment create(@NotNull String title, @NotNull DemoCategory category,
                                             @Nullable Resource resource,
                                             @NotNull LocalTime time, int minutes)
        {
            DemoAppointment appointment = new DemoAppointment(title, category, resource);
            DateTime date = new DateTime(Today.getYear(), Today.getMonthOfYear(), Today.getDayOfMonth(),
                    time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), 0);
            appointment.setTime(date);
            appointment.setLength(Duration.standardMinutes(minutes));

            return appointment;
        }
    }
}