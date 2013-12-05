/*
GanttProject is an opensource project management tool.
Copyright (C) 2011 GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject.parser;


import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import net.sourceforge.ganttproject.GPLogger;
import net.sourceforge.ganttproject.io.GanttXMLOpen;

import org.xml.sax.Attributes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import biz.ganttproject.core.calendar.CalendarEvent;
import biz.ganttproject.core.calendar.GPCalendar;
import biz.ganttproject.core.time.CalendarFactory;

/**
 * @author nbohn
 */
public class HolidayTagHandler implements TagHandler, ParsingListener {
  private final GPCalendar myCalendar;
  private final List<CalendarEvent> myEvents = Lists.newArrayList();

  public HolidayTagHandler(GPCalendar calendar) {
    myCalendar = calendar;
    myCalendar.clearPublicHolidays();
  }

  /**
   * @see net.sourceforge.ganttproject.parser.TagHandler#endElement(String,
   *      String, String)
   */
  @Override
  public void endElement(String namespaceURI, String sName, String qName) {
  }

  /**
   * @see net.sourceforge.ganttproject.parser.TagHandler#startElement(String,
   *      String, String, Attributes)
   */
  @Override
  public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) {
    if (qName.equals("date")) {
      loadHoliday(attrs);
    }
  }

  private void loadHoliday(Attributes atts) {
    try {
      String yearAsString = atts.getValue("year");
      String monthAsString = atts.getValue("month");
      String dayAsString = atts.getValue("date");
      int month = Integer.parseInt(monthAsString);
      int day = Integer.parseInt(dayAsString);
      if (Strings.isNullOrEmpty(yearAsString)) {
        Date date = CalendarFactory.createGanttCalendar(1, month - 1, day).getTime();
        myEvents.add(CalendarEvent.newEvent(date, true, CalendarEvent.Type.HOLIDAY, null));
      } else {
        int year = Integer.parseInt(yearAsString);
        Date date = CalendarFactory.createGanttCalendar(year, month - 1, day).getTime();
        myEvents.add(CalendarEvent.newEvent(date, false, CalendarEvent.Type.HOLIDAY, null));
      }
    } catch (NumberFormatException e) {
      GPLogger.getLogger(GanttXMLOpen.class).log(Level.WARNING, String.format("Error when parsing calendar data. Raw data: %s", atts.toString()), e);
      return;
    }

  }

  @Override
  public void parsingStarted() {
    // TODO Auto-generated method stub
  }

  @Override
  public void parsingFinished() {
    myCalendar.setPublicHolidays(myEvents);
  }
}
